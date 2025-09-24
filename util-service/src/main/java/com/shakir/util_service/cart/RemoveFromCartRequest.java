package com.shakir.util_service.cart;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RemoveFromCartRequest(
        @NotBlank(message = "Customer ID should not be empty.")
        String customerId,
        @NotBlank(message = "Product ID should not be empty.")
        String productId,
        @Min(value = 1, message = "Price must be at least 1")
        String price,
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 99999, message = "Quantity cannot exceed 99999")
        String quantity
) {
}
