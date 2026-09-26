package com.pradeesh.jobtracker.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public static class AuthRequest {
        @NotBlank
        public String username;
        @NotBlank
        public String password;
    }

    public static class AuthResponse {
        public String token;
        public AuthResponse(String token) { this.token = token; }
    }
}
