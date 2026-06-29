package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.SellerEarning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SellerEarningRepository extends JpaRepository<SellerEarning, Long> {
    List<SellerEarning> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    List<SellerEarning> findByStatusOrderByCreatedAtAsc(String status);
    List<SellerEarning> findBySellerIdAndStatus(Long sellerId, String status);
    List<SellerEarning> findBySellerIdAndCreatedAtAfter(Long sellerId, LocalDateTime since);
    boolean existsByOrderIdAndStatus(Long orderId, String status);
    long countByStatus(String status);
    List<SellerEarning> findByStatus(String status);
}
