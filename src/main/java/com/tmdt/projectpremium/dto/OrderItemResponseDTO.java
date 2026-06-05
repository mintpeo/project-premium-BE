package com.tmdt.projectpremium.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDTO {
    private Long productId;
    private String productName;
    private int quantity;
    private int price;
    private String productImg;
    private String keyCode;
}
