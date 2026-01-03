package com.gym.management.service;

import com.gym.management.entity.MealPlan;
import com.gym.management.repository.MealPlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealPlanService {
    private final MealPlanRepository mealPlanRepository;

    public MealPlanService(MealPlanRepository mealPlanRepository) {
        this.mealPlanRepository = mealPlanRepository;
    }

    public MealPlan save(MealPlan plan) {
        return mealPlanRepository.save(plan);
    }

    public List<MealPlan> findAll() {
        return mealPlanRepository.findAll();
    }
}

