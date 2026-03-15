package de.qf0xb.qticket.auth.service;

import de.qf0xb.qticket.auth.model.rbac.RoleEntity;
import de.qf0xb.qticket.security.rbac.AppPermission;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@NullMarked
public abstract class RoleService {
    public abstract List<RoleEntity> getAllRoles();
    public abstract RoleEntity getRoleByName(String name);
    public abstract RoleEntity getRoleById(UUID id);

    public abstract RoleEntity createRole(String name);
    public abstract void deleteRole(String name);

    public abstract RoleEntity addPermissionsToRole(String name, AppPermission... permission);
    public abstract Set<AppPermission> getPermissionsOfRole(String name);
    public abstract RoleEntity removePermissionsFromRole(String name, AppPermission... permission);

    public abstract RoleEntity setDescription(String name, String description);
    public abstract RoleEntity setRoleParent(String name, String parentRoleName);

}
