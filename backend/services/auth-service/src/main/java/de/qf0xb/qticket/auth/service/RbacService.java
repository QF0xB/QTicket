package de.qf0xb.qticket.auth.service;

import de.qf0xb.qticket.security.rbac.AppPermission;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Set;

@NullMarked
public abstract class RbacService {
    public abstract List<String> getRoleNamesOfUser(String login);

    public abstract Set<AppPermission> getPermissionsOfUser(String login);
}
