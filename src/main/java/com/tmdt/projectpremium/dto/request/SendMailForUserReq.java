package com.tmdt.projectpremium.dto.request;

import lombok.Data;

@Data
public class SendMailForUserReq {
    private boolean isRejected;
    private String email;
    private String des;
}