package com.shakir.inventory_service.controller;

import com.shakir.inventory_service.service.InventoryService;
import com.shakir.util_service.JsonUtils;
import com.shakir.util_service.ResponseWrapper;
import com.shakir.util_service.inventory.AddStockRequest;
import com.shakir.util_service.inventory.CheckStockRequest;
import com.shakir.util_service.inventory.InventoryResponseDTO;
import com.shakir.util_service.inventory.UpdateStockRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;
    @Autowired
    JsonUtils jsonUtils;

    @PostMapping("/addStock")
    public ResponseEntity<?> addStockForProduct(@Valid @RequestBody AddStockRequest request){
        ResponseWrapper<InventoryResponseDTO> response = new ResponseWrapper<>();
        InventoryResponseDTO inventoryResponseDTO = inventoryService.createStockForProduct(request);
        response.setData(inventoryResponseDTO);
        return ResponseEntity
                            .status(HttpStatus.CREATED)
                            .body(jsonUtils.responseWithCreated(response,"Inventory created successfully"));

    }

    @GetMapping("/allStocks")
    public ResponseEntity<?> getAllInventory() {
        List<InventoryResponseDTO> inventoryResponseDTOList = inventoryService.getAllInventory();
        return ResponseEntity.status(HttpStatus.OK)
                             .body(jsonUtils
                              .responseWithSuccess
                                      (new ResponseWrapper<>(inventoryResponseDTOList), "All the Stock"));

    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> updateStockByProductId(@PathVariable(name = "productId") String productId) {
        InventoryResponseDTO inventoryResponseDTO = inventoryService.findStockForProduct(productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(jsonUtils
                        .responseWithSuccess
                                (new ResponseWrapper<>(inventoryResponseDTO), "Stock for the Product"));

    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteStockByProductId(@PathVariable(name = "productId") String productId) {
        InventoryResponseDTO inventoryResponseDTO = inventoryService.deleteStockForProduct(productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(jsonUtils
                        .responseWithSuccess
                                (new ResponseWrapper<>(inventoryResponseDTO), "Stock for the Product"));

    }

    @PutMapping("/updateStock")
    public ResponseEntity<?> updateStock(@Valid @RequestBody UpdateStockRequest request) {
        InventoryResponseDTO inventoryResponseDTO = inventoryService.updateStock(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(jsonUtils
                        .responseWithSuccess
                                (new ResponseWrapper<>(inventoryResponseDTO), "Stock for the Product"));

    }

    @PostMapping("/checkStock")
    public ResponseEntity<?> checkStock(@Valid @RequestBody CheckStockRequest request) {
        java.lang.Boolean stockStatus = inventoryService.checkStock(request);
        if(stockStatus){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(jsonUtils
                            .responseWithSuccess
                                    (new ResponseWrapper<>(stockStatus), "Stock is available"));

        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(jsonUtils
                        .responseWithSuccess
                                (new ResponseWrapper<>(stockStatus), "Out of Stock"));

    }
}
