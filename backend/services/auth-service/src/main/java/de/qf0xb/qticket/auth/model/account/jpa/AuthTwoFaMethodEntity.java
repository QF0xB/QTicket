package de.qf0xb.qticket.auth.model.account.jpa;

import de.qf0xb.qticket.auth.model.account.TwoFaType;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

@NullMarked
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auth_two_fa_method_entity")
public class AuthTwoFaMethodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "auth_account_entity_id", nullable = false)
    private AuthAccountEntity authAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TwoFaType twoFaType; // TOTP, Passkey, OTHER

    @Column(name = "label")
    private @Nullable String label;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "last_used_at")
    private @Nullable Instant lastUsedAt;

    // For TOTP
    @Column(name = "totp_secret")
    private @Nullable String secret;

    // For passkeys
    @Column(name = "credential_id")
    private @Nullable String credentialId;

    @Column(name = "public_key")
    private @Nullable String publicKey;

    @Column(name = "sign_count")
    private @Nullable Long signCount;


}