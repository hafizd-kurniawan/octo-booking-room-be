package com.octo.booking_room.dto.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    @JsonProperty("customer_id")
    private String customerId;

    private String name;
    private String email;
    private String phone;

    @JsonProperty("isAdmin")
    private Boolean isAdmin;
}
