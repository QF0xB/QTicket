package de.qf0xb.qticket.auth.model.account;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString
@Embeddable
public class AuthAccountStatus {
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "locked", nullable = false)
    private Boolean locked = false;

    @Column(name = "locked_at")
    private Instant lockedAt;

    @Column(name = "locked_by_id")
    private UUID lockedById;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "last_failed_login_at")
    private Instant lastFailedLoginAt;

    @Column(name = "last_failed_login_ip")
    private String lastFailedLoginIp;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;
}