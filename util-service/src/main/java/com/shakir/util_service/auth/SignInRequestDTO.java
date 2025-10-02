package com.shakir.util_service.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignInRequestDTO(
        @Email
        String userName,
        @NotBlank
        String password
) {
}
