package de.qf0xb.qticket.auth.repository;

import de.qf0xb.qticket.auth.model.account.AuthAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthAccountEntityRepository extends JpaRepository<AuthAccountEntity, UUID> {
    Optional<AuthAccountEntity> findByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);

    Optional<AuthAccountEntity> findByUserId(UUID userId);
}