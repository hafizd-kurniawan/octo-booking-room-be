package com.octo.booking_room.dto.authentication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String accessToken;
    private String name;
    private String email;
    private Boolean isAdmin;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
