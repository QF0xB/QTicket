package de.qf0xb.qticket.auth.service.jpa;

import de.qf0xb.qticket.auth.model.rbac.RoleEntity;
import de.qf0xb.qticket.auth.service.AuthAccountService;
import de.qf0xb.qticket.auth.service.RbacService;
import de.qf0xb.qticket.auth.service.RoleService;
import de.qf0xb.qticket.security.rbac.AppPermission;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@NullMarked
public class JpaRbacService extends RbacService {
    private final RoleService roleService;
    private final AuthAccountService accountService;

    public JpaRbacService(RoleService roleService, AuthAccountService accountService) {
        this.roleService = roleService;
        this.accountService = accountService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getRoleNamesOfUser(String login) {
        Set<RoleEntity> roles = accountService.getRolesOfUser(login);

        return roles.stream()
                .map(RoleEntity::getName)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<AppPermission> getPermissionsOfUser(String login) {
        Set<AppPermission> permissions = new LinkedHashSet<>();
        for (RoleEntity role : accountService.getRolesOfUser(login)) {
            permissions.addAll(roleService.getPermissionsOfRole(role.getName()));
        }
        return permissions;
    }
}
