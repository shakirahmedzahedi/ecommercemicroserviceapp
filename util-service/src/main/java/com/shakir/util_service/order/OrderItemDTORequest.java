package com.shakir.util_service.order;

import java.math.BigDecimal;

public record OrderItemDTORequest(
        String productId,
        String quantity
) {

}
