package com.octo.booking_room.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Stores JWTs that have been invalidated via logout.  The token string is the
 * primary key so that lookups are efficient.
 */
@Entity
@Table(name = "token_blacklist")
@Getter
@Setter
public class TokenBlacklist {

    @Id
    // MySQL utf8mb4 allows maximum 3072‑byte index; 1024 chars × 4 bytes = 4096 and fails.
    // shrink to 768 chars (~3KB) which fits comfortably, or larger tokens can be stored
    // as a LOB if needed.
    @Column(name = "token", nullable = false, length = 768)
    private String token;

    @Column(name = "expired_at")
    private String expiredAt;
}
