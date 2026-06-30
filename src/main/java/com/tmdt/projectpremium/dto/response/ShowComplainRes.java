package com.tmdt.projectpremium.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowComplainRes {
    private long id;
    private long orderId;
    private String userName;
    private String email;
    private String reason;
    private String description;
    private String status;
    private LocalDateTime date;
    private String rejected;
    private String userSeller;
    private String emailSeller;
}