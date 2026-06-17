package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.dto.ForgotPasswordRequest;
import com.tmdt.projectpremium.dto.AuthResponse;
import com.tmdt.projectpremium.dto.LoginRequest;
import com.tmdt.projectpremium.dto.RegisterRequest;
import com.tmdt.projectpremium.dto.SendOtpRequest;
import com.tmdt.projectpremium.dto.request.LoginFacebookReq;
import com.tmdt.projectpremium.entity.User;
import com.tmdt.projectpremium.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/oauth/callback")
    public ResponseEntity<?> currentUser(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) return ResponseEntity.status(401).body("Not Logged In");
        User user = authService.createOrLoadUserOAuth(oAuth2User);
        return ResponseEntity.ok(new AuthResponse(
                true,
                "Đăng nhập thành công",
                Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "fullName", user.getFullName() != null ? user.getFullName() : "",
                        "role", user.getRole().name()
                )
        ));
    }

    @PostMapping("/facebook")
    public ResponseEntity<?> loginFacebook(@RequestBody LoginFacebookReq request) {
        User user = authService.loginFacebook(request.getAccessToken());
        return ResponseEntity.ok(new AuthResponse(
                true,
                "Đăng nhập Facebook thành công",
                Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "fullName", user.getFullName() != null ? user.getFullName() : "",
                        "role", user.getRole().name()
                )
        ));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<AuthResponse> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        authService.sendRegisterOtp(request);
        return ResponseEntity.ok(new AuthResponse(true, "Mã xác nhận đã được gửi đến email của bạn."));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.ok(new AuthResponse(
                true,
                "Đăng ký thành công! Mật khẩu đã được gửi đến email của bạn.",
                Map.of("email", user.getEmail(), "fullName", user.getFullName())
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(new AuthResponse(
                true,
                "Mật khẩu mới đã được gửi đến email của bạn."
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request);
        return ResponseEntity.ok(new AuthResponse(
                true,
                "Đăng nhập thành công",
                Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "fullName", user.getFullName() != null ? user.getFullName() : "",
                        "role", user.getRole().name()
                )
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AuthResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new AuthResponse(false, ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<AuthResponse> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.internalServerError().body(new AuthResponse(false, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AuthResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Dữ liệu không hợp lệ");
        return ResponseEntity.badRequest().body(new AuthResponse(false, message));
    }
}
