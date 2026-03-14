package de.qf0xb.qticket.user;

import de.qf0xb.qticket.user.model.UserEntity;
import de.qf0xb.qticket.user.model.UserEntityAuditInfo;
import de.qf0xb.qticket.user.model.UserEntityStatusInfo;
import de.qf0xb.qticket.user.repository.UserEntityRepository;
import de.qf0xb.qticket.user.v1.api.model.UserStatusInfo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(scanBasePackages = "de.qf0xb.qticket")
public class UserServiceApplication implements CommandLineRunner {
    static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    private PasswordEncoder passwordEncoder;
    private UserEntityRepository userEntityRepository;

    public UserServiceApplication(PasswordEncoder passwordEncoder, UserEntityRepository userEntityRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername("test");
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUserStatusInfo(new UserEntityStatusInfo());
        user.setUserAuditInfo(new UserEntityAuditInfo());
        user.getUserStatusInfo().setEmailVerified(true);
        System.out.println(userEntityRepository.save(user));
    }
}
