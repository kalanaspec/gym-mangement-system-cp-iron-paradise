package com.gym.management.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_plans")
@Data
public class PaymentPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    private String name;
    private String description;
    private String planType; // MONTHLY, YEARLY
    private BigDecimal monthlyPrice;
    private BigDecimal yearlyPrice;
    private Integer durationDays;
    private BigDecimal price;
}

