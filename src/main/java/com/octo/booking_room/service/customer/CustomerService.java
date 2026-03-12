package com.octo.booking_room.service.customer;

import com.octo.booking_room.entity.customer.Customer;

import java.util.Optional;

public interface CustomerService {
    Optional<Customer> findByEmail(String email);
    Customer findById(String customerId);
}
