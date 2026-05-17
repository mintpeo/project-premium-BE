package com.tmdt.projectpremium.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDTO {
    private String productName;
    private int quantity;
    private int price;
    private String productImg;
}
