package de.qf0xb.qticket.user.repository;

import de.qf0xb.qticket.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsernameIgnoreCaseOrEmail(String username, String email);
}