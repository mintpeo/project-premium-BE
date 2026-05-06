package com.tmdt.projectpremium.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    private Double rating;

    @Column(name = "sold")
    private Integer sold;

    @Column(name = "name")
    private String name;

    @Column(name = "price_original")
    private Integer priceOri;

    @Column(name = "price")
    private Integer price;

    @OneToMany(mappedBy = "product")
    private List<ProductCate> productCates;

    @OneToMany(mappedBy = "product")
    private List<ProductDura> productDuras;

    @OneToMany(mappedBy = "product")
    private List<ProductType> productTypes;
}



