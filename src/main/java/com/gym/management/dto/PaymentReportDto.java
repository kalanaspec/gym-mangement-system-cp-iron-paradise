package com.gym.management.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentReportDto {
    private String member;
    private BigDecimal amount;
    private LocalDate date;
    private String method;
    private String status;
}

