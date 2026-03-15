package de.qf0xb.qticket.auth.service.jpa;

import de.qf0xb.qticket.auth.model.rbac.RoleEntity;
import de.qf0xb.qticket.auth.repository.RoleEntityRepository;
import de.qf0xb.qticket.auth.service.RoleService;
import de.qf0xb.qticket.problem.exceptions.auth.RoleNotFoundException;
import de.qf0xb.qticket.security.rbac.AppPermission;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@NullMarked
public class JpaRoleService extends RoleService {
    private final RoleEntityRepository roleRepository;

    public JpaRoleService(RoleEntityRepository roleEntityRepository) {
        this.roleRepository = roleEntityRepository;
    }

    @Override
    public List<RoleEntity> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public RoleEntity getRoleByName(String name) {
        return roleRepository.findByName(normalizeRoleName(name)).orElseThrow(() ->
                new RoleNotFoundException("Role '%s' not found".formatted(normalizeRoleName(name))));
    }

    @Override
    public RoleEntity getRoleById(UUID id) {
        return roleRepository.findById(id).orElseThrow(() ->
                new RoleNotFoundException("Role with id '%s' not found".formatted(id)));
    }

    @Override
    public RoleEntity createRole(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Role name cannot be blank");
        }

        String normalizedName = name.trim().toUpperCase();

        if (roleRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("Role '%s' already exists".formatted(name));
        }

        RoleEntity role = RoleEntity.builder()
                .name(normalizedName)
                .build();

        log.debug("Created role: {}", role);

        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(String name) {
        RoleEntity existing = getRoleByName(name);
        roleRepository.delete(existing);
    }

    @Override
    @Transactional
    public RoleEntity addPermissionsToRole(String name, AppPermission... permissions) {
        RoleEntity existing = getRoleByName(name);
        existing.getAppPermission().addAll(List.of(permissions));
        return roleRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<AppPermission> getPermissionsOfRole(String name) {
        Set<AppPermission> permissions = new LinkedHashSet<>();

        RoleEntity current = getRoleByName(name);
        while (current != null) {
            permissions.addAll(current.getAppPermission());
            current = current.getParent();
        }

        return permissions;
    }



    @Override
    @Transactional
    public RoleEntity removePermissionsFromRole(String name, AppPermission... permission) {
        RoleEntity role = getRoleByName(name);
        List.of(permission).forEach(role.getAppPermission()::remove);
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public RoleEntity setDescription(String name, String description) {
        RoleEntity role = getRoleByName(name);
        role.setDescription(description);
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public RoleEntity setRoleParent(String name, String parentRoleName) {
        RoleEntity role = getRoleByName(name);
        RoleEntity parentRole = getRoleByName(parentRoleName);

        role.setParent(parentRole);

        return roleRepository.save(role);
    }

    private String normalizeRoleName(String name) {
        return name.trim().toUpperCase();
    }
}
