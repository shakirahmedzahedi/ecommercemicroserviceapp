package com.shakir.product_service.utils;

import com.shakir.product_service.dto.AddProductRequest;
import com.shakir.product_service.entity.Product;
import com.shakir.util_service.Category;
import com.shakir.util_service.ResponseWrapper;
import com.shakir.util_service.Tags;
import com.shakir.util_service.inventory.AddStockRequest;
import com.shakir.util_service.inventory.InventoryResponseDTO;
import com.shakir.util_service.inventory.UpdateStockRequest;
import com.shakir.util_service.product.ProductResponseDTO;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class ProductMapper {
    @Autowired
    private CircuitBreakerFactory<?, ?> cbFactory;
    @Autowired
    private RetryRegistry retryRegistry;
    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    InventoryFallBackHandler inventoryFallBackHandler;

    @Value("${spring.webclient.inventory.baseurl}")
    private String inventoryBaseUrl;

    public ProductResponseDTO toProductResponseDTO(Product product, long stock) {

        return new ProductResponseDTO(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getAdditionalInfo(),
                product.getExtraInfo(),
                product.getPrice(),
                product.getDiscountPercentage(),
                product.getCategory().name(),
                product.getBrand(),
                product.getTag().name(),
                product.getSize(),
                product.getWeight(),
                stock,
                product.getThumbnail(),
                convertStringToList(product.getImagesList())
        );
    }

    private List<String> convertStringToList(String images) {
        return Arrays.asList(images.split(","));
    }

    public static Product extractProductFromRequest(AddProductRequest request) {
        return Product.builder()
                .id(UUID.randomUUID().toString())   // generate ID
                .title(request.title())
                .description(request.description())
                .additionalInfo(request.additionalInfo())
                .extraInfo(request.extraInfo())
                .price(new BigDecimal(request.price()))
                .discountPercentage(Double.parseDouble(request.discountPercentage()))
                .category(Category.valueOf(request.category()))
                .brand(request.brand())
                .tag(Tags.valueOf(request.tag()))
                .size(request.size())
                .weight(Long.parseLong(request.weight()))
                .thumbnail(request.thumbnail())
                .imagesList(request.imagesList())
                .build();

    }

    public ResponseWrapper<InventoryResponseDTO> mapToInventoryDTOForAddStock(AddProductRequest request, AddStockRequest addStockRequest, Product product) {
        CircuitBreaker cb = cbFactory.create("inventoryService");
        Retry retry = retryRegistry.retry("inventoryService");
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("inventoryService");

        Supplier<ResponseWrapper<InventoryResponseDTO>> inventoryCall =
                RateLimiter.decorateSupplier(rateLimiter,
                        Retry.decorateSupplier(retry,
                                () -> cb.run(
                                        () -> webClientBuilder.build()
                                                .post()
                                                .uri("http://inventory-service/api/v1/inventory/addStock")
                                                .bodyValue(addStockRequest)
                                                .retrieve()
                                                .bodyToMono(new ParameterizedTypeReference<ResponseWrapper<InventoryResponseDTO>>() {})
                                                .block(),
                                        throwable -> inventoryFallBackHandler.inventoryFallBackForUpdateStock(product, Long.parseLong(request.stock()), throwable)
                                )));

        return inventoryCall.get();
    }

    public ProductResponseDTO mapToInventoryDTOForGetStockByProductId(Product product){
        CircuitBreaker cb = cbFactory.create("InventoryService");
        Retry retry = retryRegistry.retry("InventoryService");
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("InventoryService");

        Supplier<ResponseWrapper<InventoryResponseDTO>> supplier = RateLimiter.decorateSupplier(rateLimiter,
                Retry.decorateSupplier(retry,
                        ()-> cb.run(
                                ()-> webClientBuilder.build()
                                        .get()
                                        .uri("http://inventory-service/api/v1/inventory/{productId}", product.getId())
                                        .retrieve()
                                        .bodyToMono(new ParameterizedTypeReference<ResponseWrapper<InventoryResponseDTO>>() {
                                        })
                                        .block(),
                                throwable -> inventoryFallBackHandler.inventoryFallBackForGetStockByProductId(product.getId(), throwable)
                        )));

        ResponseWrapper<InventoryResponseDTO> inventorySupplier = supplier.get();
        return toProductResponseDTO(product, inventorySupplier.getData().quantity());

    }

    public ProductResponseDTO mapToInventoryDTOForUpdateStock(Product product, long quantity){
        CircuitBreaker cb = cbFactory.create("InventoryService");
        Retry retry = retryRegistry.retry("InventoryService");
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("InventoryService");
        UpdateStockRequest updateStockRequest = new UpdateStockRequest(product.getId(), quantity);

        Supplier<ResponseWrapper<InventoryResponseDTO>> supplier = RateLimiter.decorateSupplier(rateLimiter,
                Retry.decorateSupplier(retry,
                        ()-> cb.run(
                                ()-> webClientBuilder.build()
                                        .put()
                                        .uri("http://inventory-service/api/v1/inventory/updateStock")
                                        .bodyValue(updateStockRequest)
                                        .retrieve()
                                        .bodyToMono(new ParameterizedTypeReference<ResponseWrapper<InventoryResponseDTO>>() {
                                        })
                                        .block(),
                                throwable -> inventoryFallBackHandler.inventoryFallBackForUpdateStock(product,quantity, throwable)
                        )));

        ResponseWrapper<InventoryResponseDTO> inventorySupplier = supplier.get();

        return toProductResponseDTO(product, inventorySupplier.getData().quantity());

    }

    public void mapToInventoryDTOForDeleteStock(String productId){
        CircuitBreaker cb = cbFactory.create("InventoryService");
        Retry retry = retryRegistry.retry("InventoryService");
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("InventoryService");

        Supplier<ResponseWrapper<InventoryResponseDTO>> supplier = RateLimiter.decorateSupplier(rateLimiter,
                Retry.decorateSupplier(retry,
                        () -> cb.run(
                                () -> webClientBuilder.build()
                                        .delete()
                                        .uri("http://inventory-service/api/v1/inventory/{productId}", productId)
                                        .retrieve()
                                        .bodyToMono(new ParameterizedTypeReference<ResponseWrapper<InventoryResponseDTO>>() {
                                        })
                                        .block(),
                                throwable -> inventoryFallBackHandler.inventoryFallBackForDeleteStock(productId)
                        )));
    }
}
