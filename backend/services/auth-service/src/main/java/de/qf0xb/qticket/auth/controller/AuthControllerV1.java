package de.qf0xb.qticket.auth.controller;

import de.qf0xb.qticket.auth.v1.api.AuthApi;
import de.qf0xb.qticket.auth.v1.api.model.LoginRequest;
import de.qf0xb.qticket.auth.v1.api.model.LoginResponse;
import de.qf0xb.qticket.auth.v1.api.model.RefreshTokenRequest;
import de.qf0xb.qticket.auth.v1.api.model.TwoFaVerifyRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthControllerV1 implements AuthApi {

  @Override
  public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
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
