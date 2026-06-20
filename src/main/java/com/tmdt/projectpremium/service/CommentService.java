package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.dto.request.CommentRequest;
import com.tmdt.projectpremium.dto.response.CommentResponse;
import com.tmdt.projectpremium.entity.Comment;
import com.tmdt.projectpremium.entity.User;
import com.tmdt.projectpremium.repository.CommentRepository;
import com.tmdt.projectpremium.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRep;
    private final UserRepository userRep;

    @Transactional(readOnly = true)
    public List<CommentResponse> getByProductId(Long productId) {
        List<Comment> all = commentRep.findByProductIdOrderByCreatedAtDesc(productId);
        List<Comment> approved = all.stream().filter(Comment::isApproved).toList();

        Map<Long, CommentResponse> map = new java.util.LinkedHashMap<>();
        for (Comment c : approved) {
            CommentResponse res = CommentResponse.from(c);
            res.setReplies(new ArrayList<>());
            map.put(c.getId(), res);
        }

        List<CommentResponse> roots = new ArrayList<>();
        for (Comment c : approved) {
            CommentResponse node = map.get(c.getId());
            if (c.getParentId() == null) {
                roots.add(node);
            } else {
                CommentResponse parent = map.get(c.getParentId());
                if (parent != null) {
                    parent.getReplies().add(node);
                } else {
                    roots.add(node);
                }
            }
        }

        reverseReplies(roots);
        return roots;
    }

    private void reverseReplies(List<CommentResponse> nodes) {
        for (CommentResponse node : nodes) {
            java.util.Collections.reverse(node.getReplies());
            reverseReplies(node.getReplies());
        }
    }

    @Transactional
    public CommentResponse create(CommentRequest req) {
        User user = userRep.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        Comment comment = Comment.builder()
                .productId(req.getProductId())
                .user(user)
                .content(req.getContent())
                .parentId(req.getParentId())
                .createdAt(java.time.LocalDateTime.now())
                .approved(true)
                .build();

        return CommentResponse.from(commentRep.save(comment));
    }
}
