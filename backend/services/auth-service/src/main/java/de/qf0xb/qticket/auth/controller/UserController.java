package de.qf0xb.qticket.auth.controller;

import de.qf0xb.qticket.auth.api.UserApi;
import de.qf0xb.qticket.auth.api.model.UserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UserApi {
    @Override
    public ResponseEntity<UserInfo> getCurrentUser() {
        return UserApi.super.getCurrentUser();
    }
}
