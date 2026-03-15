package de.qf0xb.qticket.auth.service.jpa;

import de.qf0xb.qticket.auth.model.account.jpa.AuthAccountEntity;
import de.qf0xb.qticket.auth.model.account.jpa.AuthAccountStatus;
import de.qf0xb.qticket.auth.model.rbac.RoleEntity;
import de.qf0xb.qticket.auth.repository.AuthAccountEntityRepository;
import de.qf0xb.qticket.auth.service.AuthAccountService;
import de.qf0xb.qticket.auth.service.RoleService;
import de.qf0xb.qticket.problem.exceptions.auth.AuthAccountNotFoundException;
import de.qf0xb.qticket.problem.exceptions.auth.EmailUnverifiedException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;

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
        if (!passwordEncoder.matches(password, storedPasswordHash)) {
            throw new BadCredentialsException("Invalid login or password");
        }

        isAccountEnabled(login);

        return account;
    }

    @Override
    public AuthAccountEntity getAccountByAccountId(UUID id) {
        return authAccountEntityRepository.findById(id).orElseThrow(() ->
                new AuthAccountNotFoundException("Account with id '%s' not found".formatted(id))
        );
    }

    @Override
    public AuthAccountEntity getAccountByLogin(String login) {
        return authAccountEntityRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(login, login).orElseThrow(() ->
                new AuthAccountNotFoundException("Account with login '%s' not found".formatted(login))
        );
    }

    @Override
    public List<AuthAccountEntity> getAccountsByUserId(UUID userId) {
        return authAccountEntityRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAccountEnabled(String login) {
        AuthAccountEntity existing = getAccountByLogin(login);

        AuthAccountStatus status = existing.getAuthAccountStatus();
        if (status.getLocked()) {
            throw new LockedException("Account is locked");
        }

        if (!status.getEnabled()) {
            throw new DisabledException("Account is disabled");
        }

        if (!status.getEmailVerified()) {
            throw new EmailUnverifiedException("Account email is not verified");
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAccountEmailVerified(String login) {
        AuthAccountEntity existing = getAccountByLogin(login);
        return existing.getAuthAccountStatus().getEmailVerified();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAccountLocked(String login) {
        AuthAccountEntity existing = getAccountByLogin(login);
        return existing.getAuthAccountStatus().getLocked();
    }

    @Override
    @Transactional
    public AuthAccountEntity createAccount(String email, String username, String cleartextPassword, UUID userId) {
        if (authAccountEntityRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(username, email).isPresent()) {
            throw new IllegalArgumentException("Account with username or email already exists");
        }

        AuthAccountEntity account = AuthAccountEntity.builder()
                .email(email)
                .username(username)
                .passwordHash(Objects.requireNonNull(passwordEncoder.encode(cleartextPassword)))
                .userId(userId)
                .authAccountStatus(new AuthAccountStatus())
                .build();

        return authAccountEntityRepository.save(account);
    }

    @Override
    @Transactional
    public AuthAccountEntity addRoleToUser(String login, String roleName) {
        AuthAccountEntity existing = getAccountByLogin(login);
        existing.getRoleEntities().add(roleService.getRoleByName(roleName));
        return authAccountEntityRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<RoleEntity> getRolesOfUser(String login) {
        AuthAccountEntity account = getAccountByLogin(login);
        return new HashSet<>(account.getRoleEntities());
    }


    @Override
    @Transactional
    public AuthAccountEntity removeRoleFromUser(String login, String roleName) {
        AuthAccountEntity existing = getAccountByLogin(login);
        existing.getRoleEntities().remove(roleService.getRoleByName(roleName));
        return authAccountEntityRepository.save(existing);
    }

    @Override
    @Transactional
    public AuthAccountEntity setPassword(String login, String cleartextPassword) {
        AuthAccountEntity account = getAccountByLogin(login);
        account.setPasswordHash(Objects.requireNonNull(passwordEncoder.encode(cleartextPassword)));
        return authAccountEntityRepository.save(account);
    }

    @Override
    @Transactional
    public AuthAccountEntity setPasswordWithOldPassVerification(String login, String oldPassword, String cleartextPassword) {
        AuthAccountEntity account = getAccountByLogin(login);
        if (!passwordEncoder.matches(oldPassword, account.getPasswordHash())) {
            throw new BadCredentialsException("Invalid old password");
        }
        return setPassword(login, cleartextPassword);
    }

    @Override
    @Transactional
    public AuthAccountEntity setUsername(String login, String username) {
        AuthAccountEntity account = getAccountByLogin(login);
        account.setUsername(username);
        return authAccountEntityRepository.save(account);
    }

    @Override
    @Transactional
    public AuthAccountEntity setEmail(String login, String email) {
        AuthAccountEntity account = getAccountByLogin(login);
        account.setEmail(email);
        return authAccountEntityRepository.save(account);
    }

    @Override
    @Transactional
    public AuthAccountEntity setEnabled(String login, boolean enabled) {
        return updateAccountStatus(login, status -> status.setEnabled(enabled));
    }

    @Override
    @Transactional
    public AuthAccountEntity setEmailVerified(String login, boolean verified) {
        return updateAccountStatus(login, status -> status.setEmailVerified(verified));
    }

    @Override
    @Transactional
    public AuthAccountEntity setLocked(String login, boolean locked) {
        return updateAccountStatus(login, status -> status.setLocked(locked));
    }

    private AuthAccountEntity updateAccountStatus(String login, Consumer<AuthAccountStatus> updater) {
        AuthAccountEntity account = getAccountByLogin(login);
        AuthAccountStatus status = account.getAuthAccountStatus();
        updater.accept(status);
        return authAccountEntityRepository.save(account);
    }
}
