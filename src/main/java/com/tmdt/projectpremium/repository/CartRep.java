package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRep extends JpaRepository<Cart, Long> {
    Cart findByUserId(Long userId);
}
