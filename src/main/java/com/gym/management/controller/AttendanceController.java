package com.gym.management.controller;

import com.gym.management.dto.AttendanceDto;
import com.gym.management.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/log")
    public ResponseEntity<String> log(@RequestBody AttendanceDto dto) {
        attendanceService.saveAttendance(dto);
        return ResponseEntity.ok("Attendance logged");
    }
}

