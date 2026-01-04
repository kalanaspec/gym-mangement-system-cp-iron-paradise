package com.gym.management.repository;

import com.gym.management.entity.Payment;
import com.gym.management.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByEndDateBetween(LocalDate start, LocalDate end);
    List<Payment> findByMember(Members member);
    void deleteByMember(Members member);
}

