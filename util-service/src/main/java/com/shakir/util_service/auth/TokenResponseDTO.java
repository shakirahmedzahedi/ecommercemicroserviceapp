package com.shakir.util_service.auth;

public record TokenResponseDTO(
        String access_token,
        long expires_in,
        long refresh_expires_in,
        String refresh_token,
        String token_type,
        long not_before_policy,
        String session_state,
        String scope
) {
}
