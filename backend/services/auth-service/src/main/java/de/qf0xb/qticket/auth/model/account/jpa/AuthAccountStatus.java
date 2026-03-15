package de.qf0xb.qticket.auth.model.account.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

@NullMarked
@Getter
@Setter
@ToString
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthAccountStatus {
    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(name = "locked", nullable = false)
    @Builder.Default
    private Boolean locked = false;

    @Column(name = "locked_at")
    private @Nullable Instant lockedAt;

    @Column(name = "locked_by_id")
    private @Nullable UUID lockedById;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "last_failed_login_at")
    private @Nullable Instant lastFailedLoginAt;

    @Column(name = "last_failed_login_ip")
    private @Nullable String lastFailedLoginIp;

    @Column(name = "last_login_at")
    private @Nullable Instant lastLoginAt;

    @Column(name = "last_login_ip")
    private @Nullable String lastLoginIp;

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "email_verified_at")
    private @Nullable Instant emailVerifiedAt;
}