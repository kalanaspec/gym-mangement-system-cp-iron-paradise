package com.gym.management.repository;

import com.gym.management.entity.MealPlan;
import com.gym.management.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    List<MealPlan> findByMember(Members member);
    void deleteByMember(Members member);
}

