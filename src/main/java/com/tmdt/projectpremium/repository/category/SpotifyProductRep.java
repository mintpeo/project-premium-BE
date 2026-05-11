package com.tmdt.projectpremium.repository.category;

import com.tmdt.projectpremium.entity.category.SpotifyProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotifyProductRep extends JpaRepository<SpotifyProduct, Long> {}
