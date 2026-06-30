package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.dto.AuthResponse;
import com.tmdt.projectpremium.dto.ChangePasswordRequest;
import com.tmdt.projectpremium.dto.UpdateProfileRequest;
import com.tmdt.projectpremium.dto.UserProfileResponse;
import com.tmdt.projectpremium.dto.response.PointHistoryDTO;
import com.tmdt.projectpremium.entity.Order;
import com.tmdt.projectpremium.entity.OrderItem;
import com.tmdt.projectpremium.entity.User;
import com.tmdt.projectpremium.repository.OrderRep;
import com.tmdt.projectpremium.service.UserService;
import jakarta.validation.Valid;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class UserController {

    private final UserService userService;
    private final com.tmdt.projectpremium.repository.UserRepository userRep;
    private final OrderRep orderRep;

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

    @GetMapping("/points/{userId}")
    public ResponseEntity<?> getUserPoints(@PathVariable Long userId) {
        var user = userRep.findById(userId);
        if (user.isEmpty()) return ResponseEntity.badRequest().body(java.util.Map.of("error", "User not found"));
        Integer pts = user.get().getPoints();
        return ResponseEntity.ok(java.util.Map.of("points", pts != null ? pts : 0));
    }

    @GetMapping("/points/history/{userId}")
    public ResponseEntity<?> getPointHistory(@PathVariable Long userId) {
        try {
            List<Order> orders = orderRep.findPointOrdersByUserId(userId);
            List<PointHistoryDTO> history = new ArrayList<>();

            for (Order order : orders) {
                String productName = "";
                String productImg = "";
                for (OrderItem item : order.getOrderItems()) {
                    if (productName.isEmpty()) {
                        productName = item.getProduct().getName();
                        productImg = item.getProduct().getImg();
                    }
                }
                if (!productName.isEmpty()) {
                    if (order.getPointsEarned() != null && order.getPointsEarned() > 0) {
                        history.add(new PointHistoryDTO("EARNED", order.getPointsEarned(),
                                order.getId(), productName, productImg, order.getOrderDate()));
                    }
                    if (order.getPointsUsed() != null && order.getPointsUsed() > 0) {
                        history.add(new PointHistoryDTO("REDEEMED", order.getPointsUsed(),
                                order.getId(), productName, productImg, order.getOrderDate()));
                    }
                }
            }

            history.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
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
