package com.tmdt.projectpremium.entity.category;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "netflix_products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class NetflixProduct {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String img;
    private Double rating;
    private Integer sold;
    private String name;
    @Column(name = "price_original") private Integer priceOri;
    private Integer price;
}
