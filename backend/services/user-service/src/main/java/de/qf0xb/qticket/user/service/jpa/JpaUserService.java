package de.qf0xb.qticket.user.service.jpa;

import de.qf0xb.qticket.user.model.UserEntity;
import de.qf0xb.qticket.user.repository.UserEntityRepository;
import de.qf0xb.qticket.user.service.AuthenticationResult;
import de.qf0xb.qticket.user.service.UserService;
import de.qf0xb.qticket.user.v1.api.model.AuthenticationRequest;
import de.qf0xb.qticket.user.v1.api.model.AuthenticationResponse;
import de.qf0xb.qticket.user.v1.api.model.UserInfo;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JpaUserService extends UserService {
    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;

    public JpaUserService(UserEntityRepository userEntityRepository, PasswordEncoder passwordEncoder) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthenticationResult authenticate(String login, String cleartextPassword) {

    }
}
