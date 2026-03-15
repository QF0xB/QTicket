package de.qf0xb.qticket.auth.service;

import java.util.UUID;

public record SearchRequest(
        String q, // Query
        String username,
        String email,
        Boolean enabled,
        Boolean emailVerified,
        Boolean locked,
        UUID userId,
        String sort,
        Integer page,
        Integer size
) {
}
