package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.dto.AuthResponse;
import com.tmdt.projectpremium.dto.ChangePasswordRequest;
import com.tmdt.projectpremium.dto.UpdateProfileRequest;
import com.tmdt.projectpremium.dto.UserProfileResponse;
import com.tmdt.projectpremium.entity.User;
import com.tmdt.projectpremium.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile/{userId}")
    public ResponseEntity<AuthResponse> getProfile(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(new AuthResponse(
                true,
                "Lấy thông tin thành công",
                UserProfileResponse.fromUser(user)
        ));
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<AuthResponse> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        User user = userService.updateProfile(userId, request);
        return ResponseEntity.ok(new AuthResponse(
                true,
                "Cập nhật thông tin thành công",
                UserProfileResponse.fromUser(user)
        ));
    }

    @PutMapping("/change-password/{userId}")
    public ResponseEntity<AuthResponse> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok(new AuthResponse(
                true,
                "Đổi mật khẩu thành công"
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AuthResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new AuthResponse(false, ex.getMessage()));
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
