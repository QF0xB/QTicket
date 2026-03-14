package de.qf0xb.qticket.auth.model.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "auth_two_fa_method_entity")
public class AuthTwoFaMethodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "auth_account_entity_id")
    private AuthAccountEntity authAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TwoFaType twoFaType; // TOTP, Passkey, OTHER

    @Column(name = "label")
    private String label;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    // For TOTP
    @Column(name = "totp_secret")
    private String secret;

    // For passkeys
    @Column(name = "credential_id")
    private String credentialId;

    @Column(name = "public_key")
    private String publicKey;

    @Column(name = "sign_count")
    private Long signCount;


}