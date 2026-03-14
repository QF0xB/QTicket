package de.qf0xb.qticket.auth.service.jpa;

import de.qf0xb.qticket.auth.model.account.AuthAccountEntity;
import de.qf0xb.qticket.auth.model.rbac.RoleEntity;
import de.qf0xb.qticket.auth.service.AuthAccountService;
import de.qf0xb.qticket.auth.service.RbacService;
import de.qf0xb.qticket.auth.service.RoleService;
import de.qf0xb.qticket.security.rbac.AppPermission;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class JpaRbacService extends RbacService {
    private final RoleService roleService;
    private final AuthAccountService accountService;

    public JpaRbacService(RoleService roleService, AuthAccountService accountService) {
        this.roleService = roleService;
        this.accountService = accountService;
    }

    @Override
    public List<String> getRoleNamesOfUser(AuthAccountEntity account) {
        return account.getRoleEntities().stream()
                .map(RoleEntity::getName)
                .toList();
    }

    @Override
    public Set<AppPermission> getPermissionsOfUser(AuthAccountEntity account) {
        Set<AppPermission> permissions = new LinkedHashSet<>();
        for (RoleEntity role : accountService.getRolesOfUser(account)) {
            permissions.addAll(roleService.getPermissionsOfRole(role));
        }
        return permissions;
    }
}
