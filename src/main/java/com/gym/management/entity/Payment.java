package com.gym.management.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Members member;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private PaymentPlan plan;

    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // paid, pending, expired
}

