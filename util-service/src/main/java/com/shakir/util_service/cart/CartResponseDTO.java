package com.shakir.util_service.cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponseDTO(
        String id,
        String customerId,
        List<CartItemResponseDTO> items,
        BigDecimal cartPrice
) {
}
