package com.tmdt.projectpremium.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotNull(message = "productId không được để trống")
    private Long productId;

    @NotNull(message = "userId không được để trống")
    private Long userId;

    @NotNull @Min(1) @Max(5)
    private Integer stars;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;
}
