package com.shakir.util_service.order;

import com.shakir.util_service.product.ProductResponseDTO;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        String id,
        ProductResponseDTO productResponseDTO,
        long quantity,
        BigDecimal total
) {
}
