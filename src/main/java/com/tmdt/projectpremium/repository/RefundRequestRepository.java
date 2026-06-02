package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {
    List<RefundRequest> findAllByOrderByCreatedAtDesc();
    List<RefundRequest> findByStatusOrderByCreatedAtDesc(String status);
    long countByStatus(String status);
}
