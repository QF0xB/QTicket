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
public class UserEntityAuditInfo {
    @ManyToOne(optional = true)
    @JoinColumn(name = "created_by_id")
    private UserEntity createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @ManyToOne(optional = true)
    @JoinColumn(name = "last_modified_by_id")
    private UserEntity lastModifiedBy;

    @Column(name = "last_modified_at", nullable = false)
    private Instant lastModifiedAt = Instant.now();
}