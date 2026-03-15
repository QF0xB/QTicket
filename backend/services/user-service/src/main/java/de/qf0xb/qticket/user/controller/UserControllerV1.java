package de.qf0xb.qticket.user.controller;

import de.qf0xb.qticket.user.v1.api.UserApi;
import de.qf0xb.qticket.user.v1.api.model.AuthenticationRequest;
import de.qf0xb.qticket.user.v1.api.model.AuthenticationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserControllerV1 implements UserApi {
    @Override
    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(null);
    }
}
