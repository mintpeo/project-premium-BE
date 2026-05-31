package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRep extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByProduct_Seller_Id(Long sellerId);
}
