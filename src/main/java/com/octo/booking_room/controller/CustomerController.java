package com.octo.booking_room.controller;

import com.octo.booking_room.entity.customer.Customer;
import com.octo.booking_room.service.customer.CustomerService;
import com.octo.booking_room.utils.WebResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebResponse<Customer>> getCustomer(@PathVariable("id") String id) {
        Customer customer = customerService.findById(id);
        return ResponseEntity.ok(new WebResponse<>("success", "Customer retrieved", customer));
    }
}
