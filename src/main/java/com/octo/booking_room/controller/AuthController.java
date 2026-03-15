package com.octo.booking_room.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.octo.booking_room.dto.ApiResponse;
import com.octo.booking_room.dto.authentication.AuthResponse;
import com.octo.booking_room.dto.authentication.LoginRequest;
import com.octo.booking_room.dto.authentication.RegisterRequest;
import com.octo.booking_room.dto.authentication.RegisterResponse;
// import com.octo.booking_room.repository.CustomerRepository;
import com.octo.booking_room.service.AuthService;
// import com.octo.booking_room.util.JwtUtil;

// import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        
        RegisterResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<RegisterResponse>("success", "Customer registered succesfully", response)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(
            new ApiResponse<AuthResponse>("success", "Login successful", response)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization header must include Bearer token");
        }
        String token = authHeader.substring(7);
        boolean loggedOut = authService.logout(token);
        if (!loggedOut) {
            return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<String>("success", "Token already logged out", null)
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
            new ApiResponse<String>("success", "Logout successful", null)
        );
    }
}
