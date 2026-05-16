package com.tmdt.projectpremium.dto.request;

import lombok.Getter;

@Getter
public class OrderItemReq {
    private long orderId;
    private long productId;
    private int quantity;
    private String typeUser;
    private String duration;
}
