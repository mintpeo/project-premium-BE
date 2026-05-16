package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRep extends JpaRepository<Order, Long> {
}
