package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.ProductKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductKeyRepository extends JpaRepository<ProductKey, Long> {
    List<ProductKey> findByProductId(Long productId);
    List<ProductKey> findBySoldFalseAndProductId(Long productId);
    long countByProductId(Long productId);
    long countBySoldFalseAndProductId(Long productId);
    long countBySoldTrue();
    long countBySoldFalse();
}
