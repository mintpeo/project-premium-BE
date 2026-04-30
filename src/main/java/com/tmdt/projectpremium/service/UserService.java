package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.dto.ChangePasswordRequest;
import com.tmdt.projectpremium.dto.UpdateProfileRequest;
import com.tmdt.projectpremium.entity.User;
import com.tmdt.projectpremium.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
    }

    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = getUserById(userId);
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        return userRepository.save(user);
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }

        User user = getUserById(userId);

        // Kiểm tra mật khẩu hiện tại
        boolean currentPasswordMatch = false;
        try {
            currentPasswordMatch = passwordEncoder.matches(request.getCurrentPassword(), user.getPassword());
        } catch (Exception ignored) {}

        if (!currentPasswordMatch) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
