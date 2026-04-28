package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.dto.ForgotPasswordRequest;
import com.tmdt.projectpremium.dto.RegisterRequest;
import com.tmdt.projectpremium.dto.SendOtpRequest;
import com.tmdt.projectpremium.entity.User;
import com.tmdt.projectpremium.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpService otpService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final int PASSWORD_LENGTH = 10;

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
}
