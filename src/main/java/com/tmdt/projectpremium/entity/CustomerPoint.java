package com.tmdt.projectpremium.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_points")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CustomerPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User seller;

    @Column(nullable = false)
    private Integer points = 0;

    @Column(name = "total_earned")
    private Integer totalEarned = 0;

    @Column(name = "total_redeemed")
    private Integer totalRedeemed = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
