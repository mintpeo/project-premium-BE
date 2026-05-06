package com.tmdt.projectpremium.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoRes {
    private long id;
    private String img;
    private Double rating;
    private Integer sold;
    private String name;
    private Integer priceOri;
    private Integer price;
    private List<String> categories;
    private List<String> duration;
    private List<String> typesUser;
}
