package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.dto.AuthResponse;
import com.tmdt.projectpremium.dto.RegisterRequest;
import com.tmdt.projectpremium.entity.User;
import com.tmdt.projectpremium.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.ok(new AuthResponse(
                true,
                "Đăng ký thành công! Mật khẩu đã được gửi đến email của bạn.",
                Map.of("email", user.getEmail(), "fullName", user.getFullName())
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
