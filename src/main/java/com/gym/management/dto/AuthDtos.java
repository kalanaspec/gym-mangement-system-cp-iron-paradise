package com.gym.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthDtos {
    @Data
    public static class RegisterRequest {
        @NotBlank
        private String username;
        @NotBlank
        @Size(min = 6)
        private String password;
        @NotBlank
        private String name;
        @Email
        private String email;
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }

    @Data
    public static class JwtResponse {
        private String token;
        public JwtResponse(String token) { this.token = token; }
    }
}

