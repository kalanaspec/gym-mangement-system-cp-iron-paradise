package com.gym.management.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Data
public class Members {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(unique = true)
    private String admissionNumber;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private String address;
    private LocalDate dateOfBirth;
    private Double height;
    private Double weight;
    private String gender;
    private String phoneNumber;
    private LocalDateTime registrationDate;
    private String status; // pending, active, inactive
    
    // Payment related fields
    private String paymentStatus; // PAID, UNPAID, PENDING
    private String paymentPlanType; // MONTHLY, YEARLY
    private BigDecimal paymentAmount;
    private LocalDateTime lastPaymentDate;
    private LocalDateTime nextPaymentDate;
}

