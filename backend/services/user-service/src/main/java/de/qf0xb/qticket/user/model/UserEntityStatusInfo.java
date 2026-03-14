package de.qf0xb.qticket.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
@Embeddable
public class UserEntityStatusInfo {
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "locked", nullable = false)
    private Boolean locked = false;

    @Column(name = "locked_at")
    private Instant lockedAt;

    @ManyToOne
    @JoinColumn(name = "locked_by_id")
    private UserEntity lockedBy;

    @Column(name = "expired", nullable = false)
    private Boolean expired = false;

    @Column(name = "expired_at")
    private Instant expiredAt;
}