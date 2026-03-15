package de.qf0xb.qticket.auth.repository;

import de.qf0xb.qticket.auth.model.account.jpa.AuthTwoFaMethodEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@NullMarked
public interface AuthTwoFaMethodEntityRepository extends JpaRepository<AuthTwoFaMethodEntity, UUID> {
}