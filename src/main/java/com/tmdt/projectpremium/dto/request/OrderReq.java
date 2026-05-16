package com.tmdt.projectpremium.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderReq {
    private long userId;
    private String fullName;
    private String phoneNumber;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private LocalDateTime orderDate;
    private String note;
    private int totalPrice;
}
