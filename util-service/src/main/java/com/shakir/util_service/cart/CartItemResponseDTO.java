package com.shakir.util_service.cart;

import com.shakir.util_service.product.ProductResponseDTO;

import java.math.BigDecimal;

public record CartItemResponseDTO(
        String id,
        ProductResponseDTO productResponseDTO,
        long quantity,
        BigDecimal itemPrice
) {
}
