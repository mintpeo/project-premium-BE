package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRep extends JpaRepository<Order, Long> {
    List<Order> findByUserIdAndOrderStatusOrderByIdDesc(Long userId, String status);
}
