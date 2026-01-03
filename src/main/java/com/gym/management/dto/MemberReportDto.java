package com.gym.management.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MemberReportDto {
    private String name;
    private String email;
    private String type;
    private LocalDateTime joinDate;
    private String plan;
    private String status;
}

