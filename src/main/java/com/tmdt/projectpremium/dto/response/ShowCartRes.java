package com.tmdt.projectpremium.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowCartRes {
    private long id;
    private long productId;
    private String productName;
    private String productImg;
    private int productPrice;
    private int quantity;
    private String typeUser;
    private String duration;
}
