package com.tmdt.projectpremium.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "seller_balances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "seller_id", nullable = false, unique = true)
    private User seller;

    @Column(name = "pending_amount", nullable = false)
    private long pendingAmount = 0;

    @Column(name = "available_amount", nullable = false)
    private long availableAmount = 0;

    @Column(name = "total_earned", nullable = false)
    private long totalEarned = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
