package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {
    List<RefundRequest> findAllByOrderByCreatedAtDesc();
    List<RefundRequest> findByStatusOrderByCreatedAtDesc(String status);
    long countByStatus(String status);

    @Query("SELECT r FROM RefundRequest r WHERE r.order.id IN " +
           "(SELECT oi.order.id FROM OrderItem oi WHERE oi.product.seller.id = :sellerId) " +
           "ORDER BY r.createdAt DESC")
    List<RefundRequest> findBySellerId(@Param("sellerId") Long sellerId);
}
