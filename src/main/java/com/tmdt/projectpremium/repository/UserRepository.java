package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(User.Role role);
    long countByCreatedAtAfter(java.time.LocalDateTime since);
}
