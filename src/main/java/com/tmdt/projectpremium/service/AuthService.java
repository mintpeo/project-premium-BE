package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.dto.ForgotPasswordRequest;
import com.tmdt.projectpremium.dto.LoginRequest;
import com.tmdt.projectpremium.dto.RegisterRequest;
import com.tmdt.projectpremium.dto.SendOtpRequest;
import com.tmdt.projectpremium.dto.request.LoginGoogleReq;
import com.tmdt.projectpremium.entity.User;
import com.tmdt.projectpremium.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpService otpService;
    private final ObjectMapper objectMapper;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final int PASSWORD_LENGTH = 10;

    public User createOrLoadUserOAuth(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            String id = oAuth2User.getAttribute("sub");
            if (id == null) id = oAuth2User.getAttribute("id");
            email = id + "@facebook.local";
            if (name == null) name = oAuth2User.getAttribute("id");
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            User newUser = User.builder()
                    .fullName(name)
                    .email(email)
                    .password(passwordEncoder.encode("OAUTH_LOGIN"))
                    .role(User.Role.CUSTOMER)
                    .build();
            return userRepository.save(newUser);
        }
        return user;
    }

    public LoginGoogleReq infoUser(OAuth2User user) {
        OAuth2User userGoogle = loginGoogle(user);
        LoginGoogleReq req = new LoginGoogleReq();
        req.setEmail(userGoogle.getAttribute("email"));
        req.setName(userGoogle.getAttribute("name"));
        return req;
    }

    public OAuth2User loginGoogle(@AuthenticationPrincipal OAuth2User user) {
        return user;
    }

    public User loginFacebook(String accessToken) {
        try {
            URL url = new URL("https://graph.facebook.com/me?fields=id,name,email&access_token=" + accessToken);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            @SuppressWarnings("unchecked")
            Map<String, Object> fbData = objectMapper.readValue(conn.getInputStream(), Map.class);

            String fbId = (String) fbData.get("id");
            String name = (String) fbData.get("name");
            String email = (String) fbData.get("email");

            if (email == null) email = fbId + "@facebook.local";
            if (name == null) name = fbId;

            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                user = User.builder()
                        .fullName(name)
                        .email(email)
                        .password(passwordEncoder.encode("FACEBOOK_LOGIN"))
                        .role(User.Role.CUSTOMER)
                        .build();
            } else {
                user.setFullName(name);
            }
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Xác thực Facebook thất bại: " + e.getMessage());
        }
    }

    // Gửi OTP về email để xác nhận đăng ký
    public void sendRegisterOtp(SendOtpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }
        String otp = otpService.generateOtp(request.getEmail());
        try {
            emailService.sendOtpEmail(request.getEmail(), request.getFullName(), otp);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.");
        }
    }

    // Xác nhận OTP và tạo tài khoản
    public User register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }
        if (!otpService.verifyOtp(request.getEmail(), request.getOtp())) {
            throw new IllegalArgumentException("Mã xác nhận không đúng hoặc đã hết hạn");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(User.Role.CUSTOMER)
                .build();

        User savedUser = userRepository.save(user);
        otpService.removeOtp(request.getEmail());
        return savedUser;
    }

    // Quên mật khẩu
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại trong hệ thống"));

        String newPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        try {
            emailService.sendResetPasswordEmail(user.getEmail(), user.getFullName(), newPassword);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.");
        }
    }

    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }

    // Đăng nhập
    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email hoặc mật khẩu không đúng"));

        String storedPassword = user.getPassword();
        String inputPassword = request.getPassword();

        // Hỗ trợ cả plain text (legacy) lẫn bcrypt
        boolean passwordMatch = false;

        // Kiểm tra bcrypt (password bắt đầu bằng $2a$ hoặc $2b$)
        if (storedPassword != null && (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$"))) {
            try {
                passwordMatch = passwordEncoder.matches(inputPassword, storedPassword);
            } catch (Exception ignored) {}
        } else {
            // Plain text comparison
            passwordMatch = inputPassword.equals(storedPassword);
        }

        if (!passwordMatch) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng");
        }

        // Nếu password đang là plain text, tự động hash lại
        if (!storedPassword.startsWith("$2a$") && !storedPassword.startsWith("$2b$")) {
            user.setPassword(passwordEncoder.encode(inputPassword));
            userRepository.save(user);
        }

        return user;
    }

    // Fix: hash lại password plain text trong DB
    public void fixPlainTextPassword(String email, String plainPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));
        user.setPassword(passwordEncoder.encode(plainPassword));
        userRepository.save(user);
    }
}
