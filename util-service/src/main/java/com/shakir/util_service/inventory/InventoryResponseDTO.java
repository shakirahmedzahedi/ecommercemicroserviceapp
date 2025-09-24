package com.shakir.util_service.inventory;

public record InventoryResponseDTO(
        String id,
        String productId,
        long quantity
) {
}
