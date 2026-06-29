package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.WithdrawRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawRequestRepository extends JpaRepository<WithdrawRequest, Long> {
    List<WithdrawRequest> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    List<WithdrawRequest> findByStatusOrderByCreatedAtDesc(String status);
}
