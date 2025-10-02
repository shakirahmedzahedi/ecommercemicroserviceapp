package com.shakir.util_service.auth;

import com.shakir.util_service.order.AddressDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterCustomerRequestDTO(
        @NotBlank(message = "First name required")
        String firstName,
        @NotBlank(message = "Last name required")
        String lastName,
        @Email(message = "Invalid email")
        String email,
        @NotBlank(message = "password is required")
        String password,
        AddressDTO address
) {
}
