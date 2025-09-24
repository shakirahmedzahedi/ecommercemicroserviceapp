package com.shakir.util_service.inventory;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddStockRequest(
        @NotBlank(message = "Product Id cannot be empty")
        @Size(max = 36, message = "Product ID cannot be exceed 16 character")
        String productId,
        @Min(value = 1, message = "Stock must be at least 1")
        @Max(value = 99999, message = "Stock cannot exceed 99999")
        long quantity
) {
}
