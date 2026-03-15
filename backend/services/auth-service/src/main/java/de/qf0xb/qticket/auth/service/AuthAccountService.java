package de.qf0xb.qticket.auth.service;

import de.qf0xb.qticket.auth.model.account.jpa.AuthAccountEntity;
import de.qf0xb.qticket.auth.model.rbac.RoleEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@NullMarked
public abstract class AuthAccountService {
    public abstract AuthAccountEntity authenticate(String login, String password);

    public abstract AuthAccountEntity getAccountByAccountId(UUID id);
    public abstract AuthAccountEntity getAccountByLogin(String login);

    public abstract List<AuthAccountEntity> getAccountsByUserId(UUID userId);

    public abstract Page<AuthAccountEntity> searchAccounts(SearchRequest request);

    public abstract boolean isAccountEnabled(String login);
    public abstract boolean isAccountEmailVerified(String login);
    public abstract boolean isAccountLocked(String login);

    public abstract AuthAccountEntity createAccount(String email, String username, String cleartextPassword, UUID userId);

    public abstract AuthAccountEntity addRoleToUser(String login, String roleName);
    public abstract Set<RoleEntity> getRolesOfUser(String login);
    public abstract AuthAccountEntity removeRoleFromUser(String login, String roleName);

    public abstract AuthAccountEntity setPassword(String login, String cleartextPassword);
    public abstract AuthAccountEntity setPasswordWithOldPassVerification(String login, String oldPassword, String cleartextPassword);

    public abstract AuthAccountEntity setUsername(String login, String username);
    public abstract AuthAccountEntity setEmail(String login, String email);

    public abstract AuthAccountEntity setEnabled(String login, boolean enabled);
    public abstract AuthAccountEntity setEmailVerified(String login, boolean verified);
    public abstract AuthAccountEntity setLocked(String login, boolean locked);
}
