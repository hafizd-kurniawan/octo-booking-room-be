package com.octo.booking_room.service.customer;

import com.octo.booking_room.dto.customer.CustomerResponse;
import com.octo.booking_room.entity.customer.Customer;
import com.octo.booking_room.exception.ResourceNotFoundException;
import com.octo.booking_room.repository.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public CustomerResponse findById(String customerId) {
        Customer cust = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with ID " + customerId + " not found"));
        return new CustomerResponse(
                cust.getCustomerId(),
                cust.getName(),
                cust.getEmail(),
                cust.getPhone()
        );
    }
}
