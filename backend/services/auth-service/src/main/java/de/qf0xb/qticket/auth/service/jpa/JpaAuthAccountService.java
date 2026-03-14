package de.qf0xb.qticket.auth.service.jpa;

import de.qf0xb.qticket.auth.model.account.AuthAccountEntity;
import de.qf0xb.qticket.auth.model.account.AuthAccountStatus;
import de.qf0xb.qticket.auth.model.rbac.RoleEntity;
import de.qf0xb.qticket.auth.repository.AuthAccountEntityRepository;
import de.qf0xb.qticket.auth.service.AuthAccountService;
import de.qf0xb.qticket.auth.service.RoleService;
import de.qf0xb.qticket.problem.exceptions.auth.AuthAccountNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@NullMarked
public class JpaAuthAccountService extends AuthAccountService {
    private final AuthAccountEntityRepository authAccountEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public JpaAuthAccountService(AuthAccountEntityRepository authAccountEntityRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.authAccountEntityRepository = authAccountEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthAccountEntity authenticate(String login, String password) {
        AuthAccountEntity account = null;
        try {
            account = getAccountByLogin(login);
        } catch (AuthAccountNotFoundException e) {
            throw new BadCredentialsException("Invalid login or password");
        }

        String storedPasswordHash = account.getPasswordHash();
        if (storedPasswordHash == null || !passwordEncoder.matches(password, storedPasswordHash)) {
            throw new BadCredentialsException("Invalid login or password");
        }

        isAccountEnabled(account);

        return account;
    }

    @Override
    public AuthAccountEntity getAccountById(UUID id) {
        return authAccountEntityRepository.findById(id).orElseThrow(() ->
                new AuthAccountNotFoundException("Account with id '%s' not found".formatted(id))
        );
    }

    @Override
    public AuthAccountEntity getAccountByUserId(UUID id) {
        return authAccountEntityRepository.findByUserId(id).orElseThrow(() ->
                new AuthAccountNotFoundException("Account with id '%s' not found".formatted(id)));
    }

    @Override
    public AuthAccountEntity getAccountByLogin(String login) {
        return authAccountEntityRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(login, login).orElseThrow(() ->
                new AuthAccountNotFoundException("Account with login '%s' not found".formatted(login))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAccountEnabled(UUID id) {
        return isAccountEnabled(getAccountById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAccountEnabled(String login) {
        return isAccountEnabled(getAccountByLogin(login));
    }

    @Override
    public boolean isAccountEnabled(AuthAccountEntity acc) {
        AuthAccountEntity existing = null;
        try {
            existing = getAccountById(acc.getId());
        } catch (AuthAccountNotFoundException e) {
            throw new BadCredentialsException("Invalid login or password");
        }

        AuthAccountStatus status = existing.getAuthAccountStatus();
        if (status == null) {
            throw new DisabledException("Account status is not available");
        }

        if (Boolean.TRUE.equals(status.getLocked())) {
            throw new LockedException("Account is locked");
        }

        if (!Boolean.TRUE.equals(status.getEnabled())) {
            throw new DisabledException("Account is disabled");
        }

        if (!Boolean.TRUE.equals(status.getEmailVerified())) {
            throw new DisabledException("Account email is not verified");
        }

        return true;
    }

    @Override
    public AuthAccountEntity createAccount(String email, String username, String cleartextPassword, UUID userId) {
        AuthAccountEntity account = new AuthAccountEntity();
        account.setEmail(email);
        account.setUsername(username);
        return createAccount(account, cleartextPassword, userId);
    }

    @Override
    public AuthAccountEntity createAccount(AuthAccountEntity account, String cleartextPassword, UUID userId) {
        if (account.getEmail() == null || account.getUsername() == null) {
            throw new IllegalArgumentException("Account email and username cannot be null");
        }

        if (account.getId() != null) {
            throw new IllegalArgumentException("Account id cannot be set directly");
        }

        if (account.getPasswordHash() != null) {
            throw new IllegalArgumentException("Account password cannot be set directly, pass cleartextPassword");
        }

        account.setUserId(userId);

        account.setPasswordHash(passwordEncoder.encode(cleartextPassword));

        if (account.getAuthAccountStatus() == null) {
            account.setAuthAccountStatus(new AuthAccountStatus());
        }

        if (authAccountEntityRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(account.getUsername(), account.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Account with username or email already exists");
        }

        return authAccountEntityRepository.save(account);
    }

    @Override
    @Transactional
    public AuthAccountEntity addRoleToUser(String login, String roleName) {
        return addRoleToUser(getAccountByLogin(login), roleService.getRoleByName(roleName));
    }

    @Override
    @Transactional
    public AuthAccountEntity addRoleToUser(AuthAccountEntity account, String roleName) {
        return addRoleToUser(account, roleService.getRoleByName(roleName));
    }

    @Override
    @Transactional
    public AuthAccountEntity addRoleToUser(String login, RoleEntity role) {
        return addRoleToUser(getAccountByLogin(login), role);
    }

    @Override
    @Transactional
    public AuthAccountEntity addRoleToUser(AuthAccountEntity account, RoleEntity role) {
        AuthAccountEntity existing = getAccountById(account.getId());
        existing.getRoleEntities().add(role);
        return authAccountEntityRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<RoleEntity> getRolesOfUser(String login) {
        return getRolesOfUser(getAccountByLogin(login));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<RoleEntity> getRolesOfUser(AuthAccountEntity account) {
        AuthAccountEntity existing = getAccountById(account.getId());

        log.info("Retrieving roles for user with id: {}", account.getId());
        log.info("Roles: {}", existing.getRoleEntities().toString());

        return existing.getRoleEntities();
    }

    @Override
    @Transactional
    public AuthAccountEntity removeRoleFromUser(String login, String roleName) {
        return removeRoleFromUser(getAccountByLogin(login), roleService.getRoleByName(roleName));
    }

    @Override
    @Transactional
    public AuthAccountEntity removeRoleFromUser(AuthAccountEntity account, String roleName) {
        return removeRoleFromUser(account, roleService.getRoleByName(roleName));
    }

    @Override
    @Transactional
    public AuthAccountEntity removeRoleFromUser(String login, RoleEntity role) {
        return removeRoleFromUser(getAccountByLogin(login), role);
    }

    @Override
    @Transactional
    public AuthAccountEntity removeRoleFromUser(AuthAccountEntity account, RoleEntity role) {
        AuthAccountEntity existing = getAccountById(account.getId());
        existing.getRoleEntities().remove(role);
        return authAccountEntityRepository.save(existing);
    }

    @Override
    public AuthAccountEntity setEmailVerified(String login, boolean verified) {
        AuthAccountEntity account = getAccountByLogin(login);
        account.getAuthAccountStatus().setEmailVerified(verified);
        return authAccountEntityRepository.save(account);
    }
}
