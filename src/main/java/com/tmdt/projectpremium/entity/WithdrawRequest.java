package com.tmdt.projectpremium.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "withdraw_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "status", nullable = false)
    private String status = "PENDING";

    @Column(name = "note")
    private String note;

    @Column(name = "admin_note")
    private String adminNote;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @ManyToOne
    @JoinColumn(name = "processed_by")
    private User processedBy;
}
