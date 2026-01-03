package com.gym.management.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AddMemberDto {
    private String name;
    private String email;
    private String username;
    private String password;
    private String address;
    private LocalDate dateOfBirth;
    private Double height;
    private Double weight;
}

