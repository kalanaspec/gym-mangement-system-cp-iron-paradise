package com.gym.management.controller;

import com.gym.management.dto.*;
import com.gym.management.service.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportsController {
    private final ReportsService reportsService;

    @GetMapping("/stats")
    public ResponseEntity<ReportsDto> getStats() {
        return ResponseEntity.ok(reportsService.getOverallStats());
    }

    @GetMapping("/members")
    public ResponseEntity<List<MemberReportDto>> getMemberReports() {
        return ResponseEntity.ok(reportsService.getMemberReports());
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentReportDto>> getPaymentReports() {
        return ResponseEntity.ok(reportsService.getPaymentReports());
    }

    @GetMapping("/attendance")
    public ResponseEntity<List<AttendanceReportDto>> getAttendanceReports() {
        return ResponseEntity.ok(reportsService.getAttendanceReports());
    }

    @GetMapping("/revenue/daily")
    public ResponseEntity<Map<String, Object>> getDailyRevenue(@RequestParam int dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Day of week must be between 1 (Monday) and 7 (Sunday)"));
        }
        BigDecimal revenue = reportsService.getDailyRevenue(dayOfWeek);
        return ResponseEntity.ok(Map.of("revenue", revenue, "dayOfWeek", dayOfWeek));
    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyRevenue(
            @RequestParam int year,
            @RequestParam int month) {
        if (month < 1 || month > 12) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Month must be between 1 and 12"));
        }
        if (year < 2000 || year > 3000) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Year must be a valid year"));
        }
        BigDecimal revenue = reportsService.getMonthlyRevenue(year, month);
        return ResponseEntity.ok(Map.of("revenue", revenue, "year", year, "month", month));
    }

    @GetMapping("/revenue/yearly")
    public ResponseEntity<Map<String, Object>> getYearlyRevenue(@RequestParam int year) {
        if (year < 2000 || year > 3000) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Year must be a valid year"));
        }
        BigDecimal revenue = reportsService.getYearlyRevenue(year);
        return ResponseEntity.ok(Map.of("revenue", revenue, "year", year));
    }
}

