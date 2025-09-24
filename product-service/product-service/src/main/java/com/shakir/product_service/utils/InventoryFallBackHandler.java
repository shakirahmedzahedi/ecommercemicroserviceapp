package com.shakir.product_service.utils;

import com.shakir.product_service.entity.PendingDeleteInventory;
import com.shakir.product_service.entity.PendingInventory;
import com.shakir.product_service.entity.Product;
import com.shakir.product_service.repository.PendingDeleteInventoryRepository;
import com.shakir.product_service.repository.PendingInventoryRepository;
import com.shakir.util_service.ErrorModel;
import com.shakir.util_service.ResponseWrapper;
import com.shakir.util_service.inventory.InventoryResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class InventoryFallBackHandler {

    @Autowired
    private PendingDeleteInventoryRepository pendingDeleteInventoryRepository;
    @Autowired
    private PendingInventoryRepository pendingInventoryRepository;

    public ResponseWrapper<InventoryResponseDTO> inventoryFallBackForUpdateStock(Product product, long stock, Throwable throwable) {

        pendingInventoryRepository.save(
                PendingInventory.builder()
                        .id(UUID.randomUUID().toString())
                        .productId(product.getId())
                        .quantity(stock)
                        .status("PENDING")
                        .build());
        return new ResponseWrapper<>(List.of(new ErrorModel(("Inventory Service Down"), "503")),
                new InventoryResponseDTO(UUID.randomUUID().toString(), product.getId(), 0L));
    }
    public ResponseWrapper<InventoryResponseDTO> inventoryFallBackForGetStockByProductId(String productId, Throwable throwable) {
        return new ResponseWrapper<>(List.of(new ErrorModel(("Inventory Service Down"), "503")),
                new InventoryResponseDTO(UUID.randomUUID().toString(), productId, 0L));
    }
    public ResponseWrapper<InventoryResponseDTO> inventoryFallBackForDeleteStock(String productId) {

        PendingDeleteInventory pendingDeleteInventory = PendingDeleteInventory.builder()
                .id(UUID.randomUUID().toString())
                .productId(productId)
                .build();
        pendingDeleteInventoryRepository.save(pendingDeleteInventory);

        return new ResponseWrapper<>(List.of(new ErrorModel(("Inventory Service Down"), "503")),
                new InventoryResponseDTO(UUID.randomUUID().toString(), productId, 0L));
    }
}
