package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRep extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long userId);
}
