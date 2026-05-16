package com.tmdt.projectpremium.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class AddOrderReq {
    private OrderReq orderInfo;
    private List<OrderItemReq> items;
}
