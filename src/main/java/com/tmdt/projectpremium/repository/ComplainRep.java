package com.tmdt.projectpremium.repository;

import com.tmdt.projectpremium.entity.Complain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplainRep extends JpaRepository<Complain, Long> {
    List<Complain> findBySellerId(long sellerId);
}