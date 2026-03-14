package de.qf0xb.qticket.auth.model.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
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