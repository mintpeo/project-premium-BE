package com.tmdt.projectpremium.dto.response;

import com.tmdt.projectpremium.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long productId;
    private Long userId;
    private String fullName;
    private Integer stars;
    private String content;
    private String createdAt;

    public static ReviewResponse from(Review r) {
        String name = r.getUser().getFullName() != null ? r.getUser().getFullName() : r.getUser().getEmail();
        String date = r.getCreatedAt() != null ? r.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
        return new ReviewResponse(r.getId(), r.getProductId(), r.getUser().getId(), name, r.getStars(), r.getContent(), date);
    }
}
