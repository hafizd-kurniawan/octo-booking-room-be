package com.octo.booking_room.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.octo.booking_room.entity.TokenBlacklist;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, String> {
    Boolean existsByToken(String token);
}
