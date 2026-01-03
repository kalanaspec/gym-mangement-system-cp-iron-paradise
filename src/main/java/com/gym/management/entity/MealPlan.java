package com.gym.management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "meal_plans")
@Data
public class MealPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mealPlanId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Members member;

    @Column(length = 4000)
    private String planText;
}

