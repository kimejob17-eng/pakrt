package com.pakrt.pakrt.responses.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long expiresIn; // dalam detik, misal 600 untuk 10 menit
    private String username;
    private String nama;
    private String role;
    private String accountId;
    private List<String> permissions;
}