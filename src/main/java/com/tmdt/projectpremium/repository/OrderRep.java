package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRep extends JpaRepository<Order, Long> {
    List<Order> findByUserIdAndOrderStatusOrderByIdDesc(Long userId, String status);
    List<Order> findAllByOrderByOrderDateDesc();
    List<Order> findByOrderStatus(String orderStatus);
    long countByOrderStatus(String orderStatus);

    @Query("SELECT o FROM Order o WHERE o.orderDate >= :since AND o.orderStatus = 'SUCCESS' ORDER BY o.orderDate ASC")
    List<Order> findSuccessOrdersSince(@Param("since") LocalDateTime since);

    long countByCouponCodeAndUserId(String couponCode, Long userId);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND (o.pointsEarned > 0 OR o.pointsUsed > 0) AND o.orderStatus = 'SUCCESS' ORDER BY o.orderDate DESC")
    List<Order> findPointOrdersByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT o.user.id FROM Order o JOIN o.orderItems i WHERE i.product.seller.id = :sellerId")
    List<Long> findCustomerIdsBySellerId(@Param("sellerId") Long sellerId);
}
