package com.gym.management.service;

import com.gym.management.dto.*;
import com.gym.management.entity.Members;
import com.gym.management.entity.Payment;
import com.gym.management.entity.Attendance;
import com.gym.management.repository.MemberRepository;
import com.gym.management.repository.PaymentRepository;
import com.gym.management.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportsService {
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;

    public ReportsDto getOverallStats() {
        ReportsDto dto = new ReportsDto();
        
        List<Members> allMembers = memberRepository.findAll();
        List<Payment> allPayments = paymentRepository.findAll();
        List<Attendance> allAttendance = attendanceRepository.findAll();
        
        // Total members
        dto.setTotalMembers((long) allMembers.size());
        
        // Calculate total revenue
        BigDecimal revenue = allPayments.stream()
            .filter(p -> "paid".equalsIgnoreCase(p.getStatus()))
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalRevenue(revenue);
        
        // Total attendance (last month)
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        long attendanceCount = allAttendance.stream()
            .filter(a -> a.getTimestamp() != null && a.getTimestamp().isAfter(oneMonthAgo.atStartOfDay()))
            .count();
        dto.setTotalAttendance(attendanceCount);
        
        // Calculate growth rate (simplified)
        if (allMembers.size() > 0) {
            long previousMonthMembers = allMembers.stream()
                .filter(m -> m.getRegistrationDate() != null && m.getRegistrationDate().isBefore(LocalDateTime.now().minusMonths(1)))
                .count();
            
            double growth = previousMonthMembers > 0 
                ? ((double)(allMembers.size() - previousMonthMembers) / previousMonthMembers) * 100 
                : 0;
            dto.setGrowthRate(growth);
        } else {
            dto.setGrowthRate(0.0);
        }
        
        // Member Report Stats
        long activeMembers = allMembers.stream()
            .filter(m -> "active".equalsIgnoreCase(m.getStatus()))
            .count();
        dto.setActiveMembers(activeMembers);
        
        long newThisMonth = allMembers.stream()
            .filter(m -> m.getRegistrationDate() != null && m.getRegistrationDate().isAfter(LocalDateTime.now().minusMonths(1)))
            .count();
        dto.setNewMembersThisMonth(newThisMonth);
        
        long pending = allMembers.stream()
            .filter(m -> "pending".equalsIgnoreCase(m.getStatus()))
            .count();
        dto.setPendingMembers(pending);
        
        // Retention rate (simplified)
        if (allMembers.size() > 0) {
            double retention = ((double) activeMembers / allMembers.size()) * 100;
            dto.setMemberRetentionRate(retention);
        } else {
            dto.setMemberRetentionRate(0.0);
        }
        
        // Payment Report Stats
        List<Payment> monthlyPayments = allPayments.stream()
            .filter(p -> p.getStartDate() != null && p.getStartDate().isAfter(LocalDate.now().minusMonths(1)))
            .collect(Collectors.toList());
        
        BigDecimal monthlyRevenue = monthlyPayments.stream()
            .filter(p -> "paid".equalsIgnoreCase(p.getStatus()))
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setMonthlyRevenue(monthlyRevenue);
        dto.setTotalTransactions((long) monthlyPayments.size());
        
        long pendingPayments = allPayments.stream()
            .filter(p -> "pending".equalsIgnoreCase(p.getStatus()))
            .count();
        dto.setPendingPayments(pendingPayments);
        
        BigDecimal avgTransaction = monthlyPayments.stream()
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (monthlyPayments.size() > 0) {
            dto.setAverageTransaction(avgTransaction.divide(BigDecimal.valueOf(monthlyPayments.size()), 2, RoundingMode.HALF_UP));
        } else {
            dto.setAverageTransaction(BigDecimal.ZERO);
        }
        
        // Attendance Report Stats
        Map<LocalDate, Long> attendanceByDate = allAttendance.stream()
            .filter(a -> a.getTimestamp() != null && a.getTimestamp().isAfter(LocalDateTime.now().minusMonths(1)))
            .collect(Collectors.groupingBy(
                a -> a.getTimestamp().toLocalDate(),
                Collectors.counting()
            ));
        
        double dailyAvg = attendanceByDate.values().stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);
        dto.setDailyAverage(dailyAvg);
        
        // Peak hour
        Map<Integer, Long> hourCounts = allAttendance.stream()
            .filter(a -> a.getTimestamp() != null)
            .collect(Collectors.groupingBy(
                a -> a.getTimestamp().getHour(),
                Collectors.counting()
            ));
        
        String peakHour = hourCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(e -> e.getKey() + ":00")
            .orElse("N/A");
        dto.setPeakHour(peakHour);
        
        // Most active day
        Map<Integer, Long> dayCounts = allAttendance.stream()
            .filter(a -> a.getTimestamp() != null)
            .collect(Collectors.groupingBy(
                a -> a.getTimestamp().getDayOfWeek().getValue(),
                Collectors.counting()
            ));
        
        Map<Integer, String> dayNames = Map.of(
            1, "Monday", 2, "Tuesday", 3, "Wednesday", 4, "Thursday",
            5, "Friday", 6, "Saturday", 7, "Sunday"
        );
        
        String mostActiveDay = dayCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(e -> dayNames.get(e.getKey()))
            .orElse("N/A");
        dto.setMostActiveDay(mostActiveDay);
        
        // Attendance growth
        if (attendanceCount > 0 && attendanceByDate.size() > 0) {
            dto.setAttendanceGrowth(5.0); // Simplified - can be calculated based on historical data
        } else {
            dto.setAttendanceGrowth(0.0);
        }
        
        return dto;
    }

    public List<MemberReportDto> getMemberReports() {
        return memberRepository.findAll().stream()
            .map(this::mapToMemberReportDto)
            .limit(10)
            .collect(Collectors.toList());
    }

    public List<PaymentReportDto> getPaymentReports() {
        return paymentRepository.findAll().stream()
            .sorted(Comparator.comparing(Payment::getStartDate).reversed())
            .limit(10)
            .map(this::mapToPaymentReportDto)
            .collect(Collectors.toList());
    }

    public List<AttendanceReportDto> getAttendanceReports() {
        return attendanceRepository.findAll().stream()
            .filter(a -> a.getTimestamp() != null) // Filter out null timestamps
            .sorted(Comparator.comparing(Attendance::getTimestamp).reversed())
            .limit(10)
            .map(this::mapToAttendanceReportDto)
            .collect(Collectors.toList());
    }

    private MemberReportDto mapToMemberReportDto(Members member) {
        MemberReportDto dto = new MemberReportDto();
        dto.setName(member.getUser() != null && member.getUser().getName() != null ? member.getUser().getName() : "Unknown");
        dto.setEmail(member.getUser() != null && member.getUser().getEmail() != null ? member.getUser().getEmail() : "N/A");
        dto.setType("Standard"); // Default
        dto.setJoinDate(member.getRegistrationDate());
        dto.setPlan("Monthly"); // Default
        dto.setStatus(member.getStatus());
        return dto;
    }

    private PaymentReportDto mapToPaymentReportDto(Payment payment) {
        PaymentReportDto dto = new PaymentReportDto();
        dto.setMember(payment.getMember() != null && payment.getMember().getUser() != null 
            ? payment.getMember().getUser().getName() 
            : "Unknown");
        dto.setAmount(payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO);
        dto.setDate(payment.getStartDate());
        dto.setMethod("Credit Card"); // Default
        dto.setStatus(payment.getStatus());
        return dto;
    }

    private AttendanceReportDto mapToAttendanceReportDto(Attendance attendance) {
        AttendanceReportDto dto = new AttendanceReportDto();
        dto.setMember(attendance.getMember() != null && attendance.getMember().getUser() != null 
            ? attendance.getMember().getUser().getName() 
            : "Unknown");
        dto.setCheckInDate(attendance.getTimestamp());
        dto.setCheckInTime(attendance.getTimestamp() != null ? attendance.getTimestamp().toLocalTime().toString() : "N/A");
        dto.setCheckOutTime("N/A"); // Not tracked
        dto.setDuration("N/A"); // Not tracked
        return dto;
    }

    /**
     * Calculate daily revenue for a specific day of the week
     * @param dayOfWeek 1=Monday, 2=Tuesday, ..., 7=Sunday
     * @return Total revenue for members who paid on that day of week (any week)
     */
    public BigDecimal getDailyRevenue(int dayOfWeek) {
        List<Members> allMembers = memberRepository.findAll();
        
        return allMembers.stream()
            .filter(m -> "PAID".equalsIgnoreCase(m.getPaymentStatus()))
            .filter(m -> m.getLastPaymentDate() != null)
            .filter(m -> m.getLastPaymentDate().getDayOfWeek().getValue() == dayOfWeek)
            .filter(m -> m.getPaymentAmount() != null)
            .map(Members::getPaymentAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate monthly revenue for a specific month and year
     * @param year The year (e.g., 2026)
     * @param month The month (1-12)
     * @return Total revenue for members who paid in that month/year
     */
    public BigDecimal getMonthlyRevenue(int year, int month) {
        List<Members> allMembers = memberRepository.findAll();
        
        return allMembers.stream()
            .filter(m -> "PAID".equalsIgnoreCase(m.getPaymentStatus()))
            .filter(m -> m.getLastPaymentDate() != null)
            .filter(m -> m.getLastPaymentDate().getYear() == year)
            .filter(m -> m.getLastPaymentDate().getMonthValue() == month)
            .filter(m -> m.getPaymentAmount() != null)
            .map(Members::getPaymentAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate yearly revenue for a specific year
     * @param year The year (e.g., 2026)
     * @return Total revenue for members who paid in that year
     */
    public BigDecimal getYearlyRevenue(int year) {
        List<Members> allMembers = memberRepository.findAll();
        
        return allMembers.stream()
            .filter(m -> "PAID".equalsIgnoreCase(m.getPaymentStatus()))
            .filter(m -> m.getLastPaymentDate() != null)
            .filter(m -> m.getLastPaymentDate().getYear() == year)
            .filter(m -> m.getPaymentAmount() != null)
            .map(Members::getPaymentAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

