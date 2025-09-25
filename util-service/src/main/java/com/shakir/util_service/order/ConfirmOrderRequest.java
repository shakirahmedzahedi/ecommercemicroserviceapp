package com.shakir.util_service.order;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ConfirmOrderRequest(
        @NotBlank(message = "Customer ID should not be empty.")
        String customerId,
        List<OrderItemDTORequest> orderItemDTORequestList,
        AddressDTO address,
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 99999, message = "Quantity cannot exceed 99999")
        String discountAmount,
        @Min(value = 1, message = "shippingCharge must be at least 1")
        @Max(value = 99999, message = "shippingCharge cannot exceed 500")
        String shippingCharge
) {
}
