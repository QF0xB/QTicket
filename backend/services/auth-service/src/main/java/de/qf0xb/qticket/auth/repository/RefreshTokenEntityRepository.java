package de.qf0xb.qticket.auth.repository;

import de.qf0xb.qticket.auth.model.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenEntityRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    boolean existsByCurrentTokenHash(String currentTokenHash);

    boolean existsByFamilyId(UUID familyId);

    Optional<RefreshTokenEntity> findByCurrentTokenHash(String refreshToken);

    List<RefreshTokenEntity> findByFamilyId(UUID familyId);

    @Modifying
    @Query("update RefreshTokenEntity r " +
            "set r.revoked = true, r.revokedAt = :at, r.revokedReason = :reason " +
            "where r.familyId = :familyId")
    void revokeFamily(UUID familyId, Instant at, String reason);
}