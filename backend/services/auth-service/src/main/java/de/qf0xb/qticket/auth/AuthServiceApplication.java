package de.qf0xb.qticket.auth;

import de.qf0xb.qticket.auth.controller.AuthApiController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
public class AuthServiceApplication {
  static void main(String[] args) {
    SpringApplication.run(AuthServiceApplication.class, args);
  }
}
