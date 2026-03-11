package de.qf0xb.qticket.auth.model.jwt;

import de.qf0xb.qticket.auth.model.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@Table(name = "refresh_token_family_entity")
public class RefreshTokenFamilyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Lob
    @Column(name = "current_token_hash", unique = true)
    private String currentTokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;



}