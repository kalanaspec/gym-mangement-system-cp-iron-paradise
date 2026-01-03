package com.gym.management.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceDto {
    private Long memberId;
    private LocalDateTime timestamp;
    private String source;
}

