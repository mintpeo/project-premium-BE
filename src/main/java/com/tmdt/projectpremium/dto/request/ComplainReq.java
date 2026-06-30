package com.tmdt.projectpremium.dto.request;

import lombok.Data;

@Data
public class ComplainReq {
    private long orderId;
    private long userId;
    private String reason;
    private String description;
    private String email;
    private long productId;
}