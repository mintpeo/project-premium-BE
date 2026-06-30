package com.tmdt.projectpremium.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProductKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"productCates", "productDuras", "productTypes", "seller", "hibernateLazyInitializer", "handler"})
    private Product product;

    @Column(name = "key_code", nullable = false)
    private String keyCode;

    @Column(name = "is_sold", nullable = false)
    private boolean sold = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    @JsonIgnore
    private OrderItem orderItem;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
