package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.SellerBalance;
import com.tmdt.projectpremium.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerBalanceRepository extends JpaRepository<SellerBalance, Long> {
    Optional<SellerBalance> findBySeller(User seller);
    Optional<SellerBalance> findBySellerId(Long sellerId);
}
