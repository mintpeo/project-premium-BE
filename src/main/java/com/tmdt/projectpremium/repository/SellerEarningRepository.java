package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.SellerEarning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellerEarningRepository extends JpaRepository<SellerEarning, Long> {
    List<SellerEarning> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    List<SellerEarning> findByStatusOrderByCreatedAtAsc(String status);
    List<SellerEarning> findBySellerIdAndStatus(Long sellerId, String status);
    boolean existsByOrderIdAndStatus(Long orderId, String status);
}
