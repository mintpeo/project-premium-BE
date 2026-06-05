package com.tmdt.projectpremium.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {
    private String orderId;
    private LocalDateTime createdAt;
    private String status;
    private int totalPrice;
    private List<OrderItemResponseDTO> items;
}
