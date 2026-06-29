package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.CustomerPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerPointRepository extends JpaRepository<CustomerPoint, Long> {
    Optional<CustomerPoint> findByUserIdAndSellerId(Long userId, Long sellerId);
    List<CustomerPoint> findBySellerIdOrderByUpdatedAtDesc(Long sellerId);
}
