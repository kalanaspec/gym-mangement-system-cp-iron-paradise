package com.gym.management.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AddMemberDto {
    private String name;
    private String email;
    private String address;
    private LocalDate dateOfBirth;
    private Double height;
    private Double weight;
    private String gender;
    private String phoneNumber;
    
    // Payment fields (optional)
    private String paymentStatus; // PAID, UNPAID, PENDING
    private String paymentPlanType; // MONTHLY, YEARLY
    private BigDecimal paymentAmount;
}

