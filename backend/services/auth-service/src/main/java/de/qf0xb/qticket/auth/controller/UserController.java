package de.qf0xb.qticket.auth.controller;

import de.qf0xb.qticket.auth.v1.api.UserApi;
import de.qf0xb.qticket.auth.v1.api.model.UserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/users")
public class UserController implements UserApi {
    @Override
    public ResponseEntity<UserInfo> getCurrentUser() {
        return UserApi.super.getCurrentUser();
    }
}
