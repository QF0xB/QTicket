package de.qf0xb.qticket.auth.controller;

import de.qf0xb.qticket.auth.api.AuthApi;
import de.qf0xb.qticket.auth.api.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController implements AuthApi {

  @Override
  public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
    System.out.println(loginRequest);
    return ResponseEntity.ok(new LoginResponse());
  }

  @Override
  public ResponseEntity<LoginResponse> verify2fa(TwoFaVerifyRequest twoFaVerifyRequest) {
    return AuthApi.super.verify2fa(twoFaVerifyRequest);
  }

  @Override
  public ResponseEntity<LoginResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
    return AuthApi.super.refreshToken(refreshTokenRequest);
  }

  @Override
  public ResponseEntity<Void> logout() {
    return ResponseEntity.ok().build();
  }
}
