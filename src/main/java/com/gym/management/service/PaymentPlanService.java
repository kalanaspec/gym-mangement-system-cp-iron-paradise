package com.gym.management.service;

import com.gym.management.entity.PaymentPlan;
import com.gym.management.repository.PaymentPlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentPlanService {
    private final PaymentPlanRepository paymentPlanRepository;

    public PaymentPlanService(PaymentPlanRepository paymentPlanRepository) {
        this.paymentPlanRepository = paymentPlanRepository;
    }

    public List<PaymentPlan> getAllPlans() {
        return paymentPlanRepository.findAll();
    }

    public Optional<PaymentPlan> getPlanById(Long id) {
        return paymentPlanRepository.findById(id);
    }

    public PaymentPlan savePlan(PaymentPlan plan) {
        return paymentPlanRepository.save(plan);
    }

    public PaymentPlan updatePlan(Long id, PaymentPlan updatedPlan) {
        Optional<PaymentPlan> existingOpt = paymentPlanRepository.findById(id);
        if (existingOpt.isPresent()) {
            PaymentPlan existing = existingOpt.get();
            existing.setName(updatedPlan.getName());
            existing.setDescription(updatedPlan.getDescription());
            existing.setPlanType(updatedPlan.getPlanType());
            existing.setMonthlyPrice(updatedPlan.getMonthlyPrice());
            existing.setYearlyPrice(updatedPlan.getYearlyPrice());
            existing.setPrice(updatedPlan.getPrice());
            return paymentPlanRepository.save(existing);
        }
        throw new RuntimeException("Payment plan not found");
    }

    public void deletePlan(Long id) {
        paymentPlanRepository.deleteById(id);
    }
}

