package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProductIdOrderByCreatedAtDesc(Long productId);
    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);
    List<Comment> findByStatusOrderByCreatedAtDesc(String status);
    List<Comment> findAllByOrderByCreatedAtDesc();
    long countByStatus(String status);
}
