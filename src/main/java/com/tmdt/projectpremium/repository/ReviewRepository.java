package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
}
