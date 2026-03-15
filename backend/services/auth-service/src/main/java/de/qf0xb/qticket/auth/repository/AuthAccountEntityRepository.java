package de.qf0xb.qticket.auth.repository;

import de.qf0xb.qticket.auth.model.account.jpa.AuthAccountEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public interface AuthAccountEntityRepository extends JpaRepository<AuthAccountEntity, UUID> {
    Optional<AuthAccountEntity> findByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);

    List<AuthAccountEntity> findByUserId(UUID userId);


}