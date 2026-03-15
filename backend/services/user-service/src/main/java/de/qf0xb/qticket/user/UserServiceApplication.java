package de.qf0xb.qticket.user;

import de.qf0xb.qticket.user.service.AuthAccountBridgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@Slf4j
@SpringBootApplication(scanBasePackages = "de.qf0xb.qticket")
public class UserServiceApplication implements CommandLineRunner {
    static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    private final AuthAccountBridgeService authAccountBridgeService;

    public UserServiceApplication(AuthAccountBridgeService authAccountBridgeService) {
        this.authAccountBridgeService = authAccountBridgeService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Creating test account...");
        UUID userId = authAccountBridgeService.createAccount("test123", "t@t.com", "test", UUID.randomUUID());
        log.info("Created test account: {}", userId);
    }
}
