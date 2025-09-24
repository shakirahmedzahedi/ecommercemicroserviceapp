package com.shakir.inventory_service.service;

import com.shakir.inventory_service.entity.Inventory;
import com.shakir.inventory_service.repository.InventoryRepository;
import com.shakir.util_service.exceptions.EntityNotFoundException;
import com.shakir.util_service.inventory.AddStockRequest;
import com.shakir.util_service.inventory.CheckStockRequest;
import com.shakir.util_service.inventory.InventoryResponseDTO;
import com.shakir.util_service.inventory.UpdateStockRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InventoryService {

    @Autowired
    InventoryRepository inventoryRepository;


    public InventoryResponseDTO createStockForProduct(@Valid AddStockRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(request.productId()).orElse(
                Inventory.builder()
                        .id(UUID.randomUUID().toString())
                        .productId(request.productId())
                        .quantity(request.quantity())
                        .build()
        );
        inventoryRepository.save(inventory);
        return toInventoryResponseDTO(inventory);
    }

    private  InventoryResponseDTO toInventoryResponseDTO(Inventory inventory){
        return new InventoryResponseDTO(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getQuantity()
        );
    }

    public List<InventoryResponseDTO> getAllInventory() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
                .map(this::toInventoryResponseDTO)
                .toList();
    }

    public InventoryResponseDTO findStockForProduct(String productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(()-> new EntityNotFoundException("Product ID is not valid."));
        return  toInventoryResponseDTO(inventory);
    }

    public InventoryResponseDTO deleteStockForProduct(String productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(()-> new EntityNotFoundException("Product ID is not valid."));
        inventoryRepository.delete(inventory);

        return toInventoryResponseDTO(inventory);
    }

    public InventoryResponseDTO updateStock( UpdateStockRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(()-> new EntityNotFoundException("Product ID is not valid."));

        inventory.setQuantity(request.quantity());
        inventoryRepository.save(inventory);

        return toInventoryResponseDTO(inventory);
    }

    public Boolean checkStock(CheckStockRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(()-> new EntityNotFoundException("Product ID is not valid."));

        return inventory.getQuantity() >= request.quantity();

    }
}
