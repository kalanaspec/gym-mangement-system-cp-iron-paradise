package com.gym.management.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AttendanceReportDto {
    private String member;
    private LocalDateTime checkInDate;
    private String checkInTime;
    private String checkOutTime;
    private String duration;
}

