package com.gym.management.controller;

import com.gym.management.dto.*;
import com.gym.management.service.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}

