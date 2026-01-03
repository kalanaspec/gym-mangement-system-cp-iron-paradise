package com.gym.management.service;

import com.gym.management.entity.Payment;
import com.gym.management.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> expiringWithinDays(int days) {
        LocalDate now = LocalDate.now();
        return paymentRepository.findByEndDateBetween(now, now.plusDays(days));
    }
}

