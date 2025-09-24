package com.shakir.product_service.jobs;


import com.shakir.product_service.entity.PendingDeleteInventory;
import com.shakir.product_service.entity.PendingInventory;

import com.shakir.product_service.repository.PendingDeleteInventoryRepository;
import com.shakir.product_service.repository.PendingInventoryRepository;
import com.shakir.util_service.ErrorModel;
import com.shakir.util_service.ResponseWrapper;
import com.shakir.util_service.inventory.AddStockRequest;
import com.shakir.util_service.inventory.InventoryResponseDTO;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
@Slf4j
@Service
public class AddInventoryScheduler {
    @Autowired
    private PendingInventoryRepository pendingInventoryRepository;
    @Autowired
    private PendingDeleteInventoryRepository pendingDeleteInventoryRepository;
    @Autowired
    private WebClient inventoryWebClient;
    @Autowired
    private CircuitBreakerFactory<?, ?> cbFactory;
    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;
    @Autowired
    private RetryRegistry retryRegistry;

    @Scheduled(fixedRate = 60000)
    public void addPendingInventory(){
        log.info("✅ Called.....");
        List<PendingInventory> pendingInventoryList = pendingInventoryRepository.findAllByStatus("PENDING");

        for (PendingInventory inventoryItem : pendingInventoryList){

                AddStockRequest request = new AddStockRequest(inventoryItem.getProductId(), inventoryItem.getQuantity());

                CircuitBreaker circuitBreaker = cbFactory.create("InventoryService");
                Retry retry = retryRegistry.retry("InventoryService");
                RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("InventoryService");

                Supplier<ResponseWrapper<InventoryResponseDTO>> supplier = RateLimiter.decorateSupplier(rateLimiter,
                        Retry.decorateSupplier(retry,
                                ()-> circuitBreaker.run(
                                        ()-> inventoryWebClient.post()
                                                .uri("/addStock")
                                                .bodyValue(request)
                                                .retrieve()
                                                .bodyToMono(new ParameterizedTypeReference<ResponseWrapper<InventoryResponseDTO>>() {})
                                                .block(),
                                        throwable -> inventoryReTryFallBack()
                                )));

                ResponseWrapper<InventoryResponseDTO> response = supplier.get();
                if(response.getErrors() == null || response.getErrors().isEmpty()){
                    pendingInventoryRepository.delete(inventoryItem);
                    log.info("✅ Synced inventory for product {} with quantity {}",
                            response.getData().productId(), response.getData().quantity());
                }

        }

    }

    @Scheduled(fixedRate = 60000)
    public void DeletePendingInventory(){
        List<PendingDeleteInventory> pendingDeleteInventoryList = pendingDeleteInventoryRepository.findAll();

        for (PendingDeleteInventory inventoryItem : pendingDeleteInventoryList){

            CircuitBreaker circuitBreaker = cbFactory.create("InventoryService");
            Retry retry = retryRegistry.retry("inventoryService");
            RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("inventoryService");

            Supplier<ResponseWrapper<InventoryResponseDTO>> supplier = RateLimiter.decorateSupplier(rateLimiter,
                    Retry.decorateSupplier(retry,
                            ()-> circuitBreaker.run(
                                    ()-> inventoryWebClient.delete()
                                            .uri("/{productId}", inventoryItem.getProductId())
                                            .retrieve()
                                            .bodyToMono(new ParameterizedTypeReference<ResponseWrapper<InventoryResponseDTO>>() {})
                                            .block(),
                                    throwable -> inventoryReTryFallBack()
                            )));

            ResponseWrapper<InventoryResponseDTO> response = supplier.get();
            if(response.getErrors().isEmpty()){
                pendingDeleteInventoryRepository.delete(inventoryItem);
            }

        }

    }
    private ResponseWrapper<InventoryResponseDTO> inventoryReTryFallBack(){
        ErrorModel errorModel = new ErrorModel("Server is not responding", "503");
        List<ErrorModel> errorModels = new ArrayList<>();
        errorModels.add(errorModel);
        log.error("❌ Failed to sync product (kept in pending). Cause");
        return new ResponseWrapper<>(errorModels, new InventoryResponseDTO("","", 0L));
    }
}
