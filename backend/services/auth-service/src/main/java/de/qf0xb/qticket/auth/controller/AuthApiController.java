package de.qf0xb.qticket.auth.controller;

import de.qf0xb.qticket.auth.api.AuthApi;
import de.qf0xb.qticket.auth.api.model.LoginRequest;
import de.qf0xb.qticket.auth.api.model.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthApiController implements AuthApi {
  @Override
  public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
    System.out.println(loginRequest);
    return ResponseEntity.ok(new LoginResponse());
  }
}
