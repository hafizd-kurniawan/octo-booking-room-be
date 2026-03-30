package com.octo.booking_room.service.customer;

import com.octo.booking_room.dto.customer.CustomerResponse;

public interface CustomerService {
    CustomerResponse findByEmail(String email);
    CustomerResponse findById(String customerId);
}
