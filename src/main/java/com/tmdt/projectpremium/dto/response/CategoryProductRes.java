package com.tmdt.projectpremium.dto.response;

import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class CategoryProductRes {
    private Long id;
    private String img;
    private Double rating;
    private Integer sold;
    private String name;
    private Integer priceOri;
    private Integer price;
}
