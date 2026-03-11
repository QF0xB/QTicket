package de.qf0xb.qticket.auth;

import de.qf0xb.qticket.auth.model.user.UserEntity;
import de.qf0xb.qticket.auth.repository.UserEntityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "de.qf0xb.qticket")
public class AuthServiceApplication implements CommandLineRunner {
  static void main(String[] args) {
    SpringApplication.run(AuthServiceApplication.class, args);
  }

  private UserEntityRepository userEntityRepository;

  public AuthServiceApplication(UserEntityRepository userEntityRepository) {
    this.userEntityRepository = userEntityRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    UserEntity userEntity = new UserEntity();
    userEntity.setFirstName("John");
    userEntity.setLastName("Doe");
    userEntity.setUsername("johndoe");
    userEntity.setEmail("john.doe@example.com");
    userEntity.setPasswordHash("password");
    userEntity = userEntityRepository.save(userEntity);

    userEntityRepository.findAll().forEach(System.out::println);
  }
}
