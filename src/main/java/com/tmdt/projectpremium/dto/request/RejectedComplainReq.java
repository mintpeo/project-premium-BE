package com.tmdt.projectpremium.dto.request;

import lombok.Data;

@Data
public class RejectedComplainReq {
    private long complainId;
    private String status;
    private String rejected;
}