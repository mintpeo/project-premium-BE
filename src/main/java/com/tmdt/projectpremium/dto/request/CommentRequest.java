package com.tmdt.projectpremium.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequest {
    @NotNull
    private Long productId;

    @NotNull
    private Long userId;

    @NotBlank
    private String content;

    private Long parentId;
}
