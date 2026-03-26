package com.octo.booking_room.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "token_blacklist")
@Getter
@Setter
public class TokenBlacklist {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "token", nullable = false, columnDefinition = "TEXT")
    private String token;

    @Column(name = "expired_at")
    private String expiredAt;
}
