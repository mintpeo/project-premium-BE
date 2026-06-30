package com.tmdt.projectpremium.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "complains")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Complain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "reason")
    private String reason;

    @Column(name = "description")
    private String description;

    @Column(name = "seller_id")
    private long sellerId;

    @Column(name = "status")
    private String status;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "reason_rejected")
    private String reasonRejected;
}