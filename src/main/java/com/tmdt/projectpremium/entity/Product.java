package com.tmdt.projectpremium.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "img")
    private String img;

    @Column(name = "rating")
    private int rating;

    @Column(name = "sold")
    private int sold;

    @Column(name = "rating_star")
    private int ratingStar;

    @Column(name = "name")
    private String name;

    @Column(name = "price_min")
    private int priceMin;

    @Column(name = "price_max")
    private int priceMax;

    @Column(name = "types_user")
    private String typesUser;

    @Column(name = "duration")
    private String duration;
}



