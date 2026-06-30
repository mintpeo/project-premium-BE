package com.tmdt.projectpremium.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PointHistoryDTO {
    private String type;
    private int points;
    private long orderId;
    private String productName;
    private String productImg;
    private LocalDateTime createdAt;
}
