package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRep extends JpaRepository<Product, Long> {
    List<Product> findByProductCates_Category_Id(long categoryId);
    List<Product> findBySellerId(Long sellerId);
    long countBySellerId(Long sellerId);
}
