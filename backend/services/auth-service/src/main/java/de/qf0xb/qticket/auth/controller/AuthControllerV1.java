package de.qf0xb.qticket.auth.controller;

import com.nimbusds.jose.jwk.RSAKey;
import de.qf0xb.qticket.auth.service.AuthService;
import de.qf0xb.qticket.auth.service.AuthenticationResult;
import de.qf0xb.qticket.auth.service.TokenService;
import de.qf0xb.qticket.auth.v1.api.AuthApi;
import de.qf0xb.qticket.auth.v1.api.model.*;
import de.qf0xb.qticket.security.rbac.AppPermission;
import de.qf0xb.qticket.security.rbac.RequirePermission;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthControllerV1 implements AuthApi {
  private final RSAKey rsaKey;
  private final AuthService authService;
  private final TokenService tokenService;

  public AuthControllerV1(RSAKey rsaKey, AuthService authService, TokenService tokenService) {
    this.rsaKey = rsaKey;
    this.authService = authService;
    this.tokenService = tokenService;
  }

  @Override
  public ResponseEntity<GetJwks200Response> getJwks() {
    GetJwks200Response getJwks200Response = new GetJwks200Response();
    Map<String, Object> jwkMap = rsaKey.toPublicJWK().toJSONObject();

    Jwk jwk = new Jwk();
    jwk.setKty(jwkMap.getOrDefault("kty", "").toString());
    jwk.setKid(jwkMap.getOrDefault("kid", "").toString());
    jwk.setN(jwkMap.getOrDefault("n", "").toString());
    jwk.setE(jwkMap.getOrDefault("e", "").toString());
    getJwks200Response.addKeysItem(jwk);
    return ResponseEntity.ok(getJwks200Response);
  }

  @Override
  public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
    AuthenticationResult result = authService.authenticate(loginRequest.getLogin(), loginRequest.getPassword());

    // Is 2fa required?
    if (result.getTwoFaChallengeInfo() != null) {
      return ResponseEntity.status(403).body(null);
    }

    LoginResponse loginResponse = new LoginResponse();
    loginResponse.setAccessToken(result.getAccessToken());
    loginResponse.setRefreshToken(result.getRefreshToken());
    loginResponse.setExpiresIn(result.getTtl());

    return ResponseEntity.ok(loginResponse);
  }

  @Override
  public ResponseEntity<LoginResponse> verify2fa(TwoFaVerifyRequest twoFaVerifyRequest) {
    return AuthApi.super.verify2fa(twoFaVerifyRequest);
  }

  @Override
  public ResponseEntity<LoginResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
    TokenService.TokenPair tokenPair = tokenService.refreshTokenPair(refreshTokenRequest.getRefreshToken());

    LoginResponse loginResponse = new LoginResponse();
    loginResponse.setAccessToken(tokenPair.accessToken());
    loginResponse.setRefreshToken(tokenPair.refreshToken());
    loginResponse.setExpiresIn(tokenPair.expiration());
    return ResponseEntity.ok(loginResponse);
  }

  @Override
  public ResponseEntity<Void> logout(LogoutRequest logoutRequest) {
    tokenService.revokeRefreshToken(logoutRequest.getRefreshToken());
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Test200Response> test() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      assert auth != null;
      System.out.println(auth.getAuthorities());
    return ResponseEntity.ok(new Test200Response());
  }
}
