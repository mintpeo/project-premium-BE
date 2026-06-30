package com.tmdt.projectpremium.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "note")
    private String note;

    @Column(name = "total_price")
    private int totalPrice;

    @Column(name = "points_used", columnDefinition = "int default 0")
    private Integer pointsUsed = 0;

    @Column(name = "points_earned", columnDefinition = "int default 0")
    private Integer pointsEarned = 0;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "discount_amount", columnDefinition = "int default 0")
    private Integer discountAmount = 0;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems;
}
