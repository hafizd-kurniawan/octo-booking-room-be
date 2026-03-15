package com.octo.booking_room.service;

import com.octo.booking_room.dto.authentication.AuthResponse;
import com.octo.booking_room.dto.authentication.LoginRequest;
import com.octo.booking_room.dto.authentication.RegisterRequest;
import com.octo.booking_room.dto.authentication.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest req);
    
    AuthResponse login(LoginRequest req);

    boolean logout(String token);
}
