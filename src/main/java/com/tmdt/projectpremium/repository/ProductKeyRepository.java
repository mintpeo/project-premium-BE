package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.ProductKey;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT pk FROM ProductKey pk WHERE pk.product.seller.id = :sellerId ORDER BY pk.createdAt DESC")
    List<ProductKey> findBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(pk) FROM ProductKey pk WHERE pk.product.seller.id = :sellerId")
    long countBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(pk) FROM ProductKey pk WHERE pk.product.seller.id = :sellerId AND pk.sold = true")
    long countBySoldTrueAndSellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(pk) FROM ProductKey pk WHERE pk.product.seller.id = :sellerId AND pk.sold = false")
    long countBySoldFalseAndSellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT pk FROM ProductKey pk WHERE pk.product.id = :productId AND pk.sold = false ORDER BY pk.createdAt ASC")
    List<ProductKey> findAvailableKeys(@Param("productId") Long productId, Pageable pageable);
}
