package com.pakrt.pakrt.controllers;

import com.pakrt.pakrt.request.auth.LoginRequest;
import com.pakrt.pakrt.responses.ResponseBuilder;
import com.pakrt.pakrt.responses.auth.AuthResponse;
import com.pakrt.pakrt.services.AuthService;
import com.pakrt.pakrt.utils.Logging;
import com.pakrt.pakrt.utils.MessageDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint Otentikasi Utama Login & Penerbitan Token Bearer 10 Menit.
     * POST /api/v1/auth/login
     */
    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseBuilder.buildSuccessResponse(response);
        } catch (IllegalArgumentException e) {
            Logging.LogError("Autentikasi gagal: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("401001", e.getMessage());
        } catch (Exception e) {
            Logging.LogError("Terjadi kesalahan internal pada server saat login: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * Endpoint Alias Standar /auth/token
     * POST /auth/token
     */
    @PostMapping("/auth/token")
    public ResponseEntity<?> token(@RequestBody LoginRequest request) {
        return login(request);
    }
}
