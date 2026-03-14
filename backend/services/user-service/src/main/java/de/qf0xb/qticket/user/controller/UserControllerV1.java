package de.qf0xb.qticket.user.controller;

import de.qf0xb.qticket.user.mapper.UserMapper;
import de.qf0xb.qticket.user.service.AuthenticationResult;
import de.qf0xb.qticket.user.service.UserService;
import de.qf0xb.qticket.user.v1.api.UserApi;
import de.qf0xb.qticket.user.v1.api.model.AuthenticationRequest;
import de.qf0xb.qticket.user.v1.api.model.AuthenticationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserControllerV1 implements UserApi {
    private final UserMapper userMapper;
    private final UserService userService;

    public UserControllerV1(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @Override
    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
        AuthenticationResult result = userService.authenticate(authenticationRequest.getLogin(), authenticationRequest.getPassword());
        AuthenticationResponse response = new AuthenticationResponse();
        response.setUser(userMapper.toUserInfo(result.user()));
        response.setRoles(result.roles());
        response.setPermissions(result.permissions());
        return ResponseEntity.ok(response);
    }
}
