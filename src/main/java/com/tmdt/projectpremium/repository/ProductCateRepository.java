package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.ProductCate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCateRepository extends JpaRepository<ProductCate, Long> {
    List<ProductCate> findByProductId(Long productId);
    void deleteByProductId(Long productId);
}