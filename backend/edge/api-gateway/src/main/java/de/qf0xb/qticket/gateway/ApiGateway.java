package de.qf0xb.qticket.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "de.qf0xb.qticket")
public class ApiGateway {
  static void main(String[] args) {
    System.out.println("Working dir = " + System.getProperty("user.dir"));
    SpringApplication.run(ApiGateway.class, args);

  }
}
