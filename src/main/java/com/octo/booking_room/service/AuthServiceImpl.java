package com.octo.booking_room.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.octo.booking_room.dto.authentication.AuthResponse;
import com.octo.booking_room.dto.authentication.LoginRequest;
import com.octo.booking_room.dto.authentication.RegisterRequest;
import com.octo.booking_room.dto.authentication.RegisterResponse;
import com.octo.booking_room.entity.customer.Customer;
import com.octo.booking_room.entity.TokenBlacklist;
import com.octo.booking_room.exception.EmailAlreadyExists;
import com.octo.booking_room.repository.customer.CustomerRepository;
import com.octo.booking_room.repository.TokenBlacklistRepository;
import com.octo.booking_room.utils.IdGenerator;
import com.octo.booking_room.utils.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {
    private final CustomerRepository customerRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(CustomerRepository customerRepository, TokenBlacklistRepository tokenBlacklistRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.customerRepository = customerRepository;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        Customer customer = new Customer();
        customer.setCustomerId(IdGenerator.generateId("customer-"));
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setIsAdmin(false);

        customerRepository.save(customer);
        return new RegisterResponse(
            customer.getCustomerId(),
            customer.getName(),
            customer.getEmail(),
            customer.getPhone(),
            customer.getIsAdmin()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new EmailAlreadyExists());

        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(customer.getEmail());
        AuthResponse response = new AuthResponse(token);

        response.setAccessToken(token);
        response.setName(customer.getName());
        response.setEmail(customer.getEmail());
        response.setIsAdmin(customer.getIsAdmin());

        return response;
    }

    @Override
    public boolean logout(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid token: cannot logout with invalid token.");
        }
        if (tokenBlacklistRepository.existsById(token)) {
            // Already logged out with this token; idempotent behavior.
            return false;
        }
        TokenBlacklist blacklistEntry = new TokenBlacklist();
        blacklistEntry.setToken(token);
        blacklistEntry.setExpiredAt(jwtUtil.getExpirationFromToken(token).toString());
        tokenBlacklistRepository.save(blacklistEntry);
        return true;
    }

}