package com.tmdt.projectpremium.dto.response;

import com.tmdt.projectpremium.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private Long productId;
    private Long userId;
    private String fullName;
    private String content;
    private Long parentId;
    private String createdAt;
    private List<CommentResponse> replies;

    public static CommentResponse from(Comment c) {
        String name = c.getUser().getFullName() != null ? c.getUser().getFullName() : c.getUser().getEmail();
        String date = c.getCreatedAt() != null ? c.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
        return new CommentResponse(c.getId(), c.getProductId(), c.getUser().getId(), name,
                c.getContent(), c.getParentId(), date, null);
    }
}
