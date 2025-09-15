package com.shakir.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductCreateRequest(
        @NotBlank(message = "Title cannot be empty")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,

        @NotBlank(message = "Description cannot be empty")
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        String price
) {}
