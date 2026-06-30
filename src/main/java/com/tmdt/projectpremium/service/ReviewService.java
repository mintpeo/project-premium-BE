package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.dto.request.ReviewRequest;
import com.tmdt.projectpremium.dto.response.ReviewResponse;
import com.tmdt.projectpremium.entity.Review;
import com.tmdt.projectpremium.entity.User;
import com.tmdt.projectpremium.repository.ReviewRepository;
import com.tmdt.projectpremium.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public List<ReviewResponse> getByProductId(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .filter(Review::isApproved)
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse create(ReviewRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        Review review = new Review();
        review.setProductId(req.getProductId());
        review.setUser(user);
        review.setStars(req.getStars());
        review.setContent(req.getContent());
        review.setCreatedAt(java.time.LocalDateTime.now());
        review.setApproved(false);

        return ReviewResponse.from(reviewRepository.save(review));
    }
}
