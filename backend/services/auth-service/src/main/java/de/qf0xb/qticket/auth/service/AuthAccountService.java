package de.qf0xb.qticket.auth.service;

import de.qf0xb.qticket.auth.model.account.AuthAccountEntity;
import de.qf0xb.qticket.auth.model.rbac.RoleEntity;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@NullMarked
public abstract class AuthAccountService {
    public abstract AuthAccountEntity authenticate(String login, String password);

    public abstract AuthAccountEntity getAccountById(UUID id);
    public abstract AuthAccountEntity getAccountByUserId(UUID id);
    public abstract AuthAccountEntity getAccountByLogin(String login);

    public abstract boolean isAccountEnabled(UUID id);
    public abstract boolean isAccountEnabled(String login);
    public abstract boolean isAccountEnabled(AuthAccountEntity account);

    public abstract AuthAccountEntity createAccount(String email, String username, String cleartextPassword, UUID userId);
    public abstract AuthAccountEntity createAccount(AuthAccountEntity account, String cleartextPassword, UUID userId);

    public abstract AuthAccountEntity addRoleToUser(String login, String roleName);
    public abstract AuthAccountEntity addRoleToUser(AuthAccountEntity account, String roleName);
    public abstract AuthAccountEntity addRoleToUser(String login, RoleEntity role);
    public abstract AuthAccountEntity addRoleToUser(AuthAccountEntity account, RoleEntity role);

    public abstract Set<RoleEntity> getRolesOfUser(String login);
    public abstract Set<RoleEntity> getRolesOfUser(AuthAccountEntity account);

    public abstract AuthAccountEntity removeRoleFromUser(String login, String roleName);
    public abstract AuthAccountEntity removeRoleFromUser(AuthAccountEntity account, String roleName);
    public abstract AuthAccountEntity removeRoleFromUser(String login, RoleEntity role);
    public abstract AuthAccountEntity removeRoleFromUser(AuthAccountEntity account, RoleEntity role);

    public abstract AuthAccountEntity setEmailVerified(String login, boolean verified);
}
