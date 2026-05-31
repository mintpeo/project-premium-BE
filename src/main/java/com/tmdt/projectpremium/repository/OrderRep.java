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
}
