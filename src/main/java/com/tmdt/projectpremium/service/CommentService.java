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

        List<Comment> topLevel = approved.stream().filter(c -> c.getParentId() == null).toList();
        List<Comment> replies = approved.stream().filter(c -> c.getParentId() != null).toList();

        Map<Long, List<CommentResponse>> replyMap = replies.stream()
                .collect(Collectors.groupingBy(
                        Comment::getParentId,
                        Collectors.mapping(CommentResponse::from, Collectors.toList())
                ));

        List<CommentResponse> result = new ArrayList<>();
        for (Comment c : topLevel) {
            CommentResponse res = CommentResponse.from(c);
            res.setReplies(replyMap.getOrDefault(c.getId(), new ArrayList<>()));
            result.add(res);
        }
        return result;
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
                .approved(true)
                .build();

        return CommentResponse.from(commentRep.save(comment));
    }
}
