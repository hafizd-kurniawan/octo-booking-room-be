package com.octo.booking_room.dto.customer;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomerResponse {
    @JsonProperty("customer_id")
    private String customerId;
    private String name;
    private String email;
    private String phone;
}
