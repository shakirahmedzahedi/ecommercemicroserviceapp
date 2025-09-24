package com.shakir.util_service.inventory;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CheckStockRequest(
        @NotBlank(message = "Product ID cannot be empty.")
        String productId,
        @Min(value = 1, message = "Quantity should be at least One")
        @Max(value = 99999, message = "Quantity should not exceed 99999")
        long quantity
) {
}
