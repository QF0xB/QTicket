package de.qf0xb.qticket.user.service;

import de.qf0xb.qticket.user.model.UserEntity;import java.util.List;

public record AuthenticationResult (
        UserEntity user,
        List<String> permissions
) { }
