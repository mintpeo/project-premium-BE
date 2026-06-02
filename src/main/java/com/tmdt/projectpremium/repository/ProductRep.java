package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRep extends JpaRepository<Product, Long> {
    List<Product> findByProductCates_Category_Id(long categoryId);
    List<Product> findBySellerId(Long sellerId);
    long countBySellerId(Long sellerId);
    List<Product> findByApprovedFalse();
    long countByApprovedFalse();

    @Query("SELECT p FROM Product p WHERE p.approved = true " +
           "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR EXISTS (SELECT pc FROM ProductCate pc WHERE pc.product = p AND pc.category.id = :categoryId)) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> searchProducts(@Param("keyword") String keyword,
                                  @Param("categoryId") Long categoryId,
                                  @Param("minPrice") Integer minPrice,
                                  @Param("maxPrice") Integer maxPrice);
}
