package de.qf0xb.qticket.auth.repository;

import de.qf0xb.qticket.auth.model.rbac.RoleEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@NullMarked
public interface RoleEntityRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByName(String name);

    boolean existsByNameIgnoreCase(String name);


}