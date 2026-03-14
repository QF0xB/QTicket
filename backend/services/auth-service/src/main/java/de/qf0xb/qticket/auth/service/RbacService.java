package de.qf0xb.qticket.auth.service;

import de.qf0xb.qticket.auth.model.account.AuthAccountEntity;
import de.qf0xb.qticket.security.rbac.AppPermission;

import java.util.List;
import java.util.Set;

public abstract class RbacService {
    public abstract List<String> getRoleNamesOfUser(AuthAccountEntity account);

    public abstract Set<AppPermission> getPermissionsOfUser(AuthAccountEntity account);
}
