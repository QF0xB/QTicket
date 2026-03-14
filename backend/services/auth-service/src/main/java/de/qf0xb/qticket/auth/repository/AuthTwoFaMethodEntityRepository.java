package de.qf0xb.qticket.auth.repository;

import de.qf0xb.qticket.auth.model.account.AuthTwoFaMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthTwoFaMethodEntityRepository extends JpaRepository<AuthTwoFaMethodEntity, UUID> {
}