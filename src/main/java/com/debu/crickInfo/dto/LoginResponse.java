package com.debu.crickInfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;           // JWT token
    private String username;        // username of logged-in user
    private String role;            // ROLE_USER or ROLE_ADMIN
    private String message;         // response message
    private boolean success;        // success flag
    private String userId;          // user's MongoDB ID
    private String name;
    private String email;
    private String franchiseName;
    private String accountStatus;
}
