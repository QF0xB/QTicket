package de.qf0xb.qticket.auth.repository;

import de.qf0xb.qticket.auth.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);
}