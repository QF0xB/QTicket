package de.qf0xb.qticket.auth.model.account.jpa;

import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "twa_fa_challenge")
public class TwaFaChallenge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "auth_account_id", nullable = false)
    private AuthAccountEntity authAccount;



}