package de.qf0xb.qticket.auth.service;

import de.qf0xb.qticket.auth.model.rbac.RoleEntity;
import de.qf0xb.qticket.security.rbac.AppPermission;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class RoleService {
    public abstract List<RoleEntity> getAllRoles();
    public abstract RoleEntity getRoleByName(String name);
    public abstract RoleEntity getRoleById(UUID id);

    public abstract RoleEntity createRole(String name);
    public abstract RoleEntity createRole(RoleEntity role);

    public abstract RoleEntity updateRole(RoleEntity role);

    public abstract RoleEntity addPermissionToRole(String name, String permission);
    public abstract RoleEntity addPermissionToRole(RoleEntity role, String permission);
    public abstract RoleEntity addPermissionToRole(String name, AppPermission permission);
    public abstract RoleEntity addPermissionToRole(RoleEntity role, AppPermission permission);

    public abstract Set<AppPermission> getPermissionsOfRole(String name);
    public abstract Set<AppPermission> getPermissionsOfRole(RoleEntity role);

    public abstract RoleEntity removePermissionFromRole(String name, String permission);
    public abstract RoleEntity removePermissionFromRole(RoleEntity role, String permission);
    public abstract RoleEntity removePermissionFromRole(String name, AppPermission permission);
    public abstract RoleEntity removePermissionFromRole(RoleEntity role, AppPermission permission);

    public abstract RoleEntity setRoleParent(String name, String parentRoleName);
    public abstract RoleEntity setRoleParent(String name, RoleEntity parentRole);
    public abstract RoleEntity setRoleParent(RoleEntity role, String parentRoleName);
    public abstract RoleEntity setRoleParent(RoleEntity role, RoleEntity parentRole);

    public abstract void deleteRole(String name);
    public abstract void deleteRole(RoleEntity role);
}
