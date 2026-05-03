package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRep extends JpaRepository<Product, Long> {
}
