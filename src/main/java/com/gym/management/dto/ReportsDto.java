package com.gym.management.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReportsDto {
    // Overall stats
    private Long totalMembers;
    private BigDecimal totalRevenue;
    private Long totalAttendance;
    private Double growthRate;

    // Member Report Stats
    private Long activeMembers;
    private Long newMembersThisMonth;
    private Long pendingMembers;
    private Double memberRetentionRate;

    // Payment Report Stats
    private BigDecimal monthlyRevenue;
    private Long totalTransactions;
    private Long pendingPayments;
    private BigDecimal averageTransaction;

    // Attendance Report Stats
    private Double dailyAverage;
    private String peakHour;
    private Double attendanceGrowth;
    private String mostActiveDay;
}

