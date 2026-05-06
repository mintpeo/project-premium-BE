package com.tmdt.projectpremium.dto.request;

import lombok.Getter;

@Getter
public class AddToCartReq {
    private Long userId;
    private Long productId;
    private int quantity;
    private String duration;
    private String typeUser;
}
