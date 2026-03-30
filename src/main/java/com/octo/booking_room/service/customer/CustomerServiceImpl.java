package com.octo.booking_room.service.customer;

import com.octo.booking_room.dto.customer.CustomerResponse;
import com.octo.booking_room.entity.customer.Customer;
import com.octo.booking_room.exception.ResourceNotFoundException;
import com.octo.booking_room.repository.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse findByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Customer with email " + email + " not found"));
        return toCustomerResponse(customer);
    }

    @Override
    public CustomerResponse findById(String customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer with ID " + customerId + " not found"));
        return toCustomerResponse(customer);
    }

    private CustomerResponse toCustomerResponse(Customer customer) {
        return new CustomerResponse(
            customer.getCustomerId(),
            customer.getName(),
            customer.getEmail(),
            customer.getPhone()
        );
    }
}
