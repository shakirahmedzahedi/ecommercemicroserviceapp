package com.shakir.apigateway_service.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product_service", r -> r.path("/api/v1/product/**")
                        .uri("lb://PRODUCT-SERVICE"))
                .route("cart_service", r -> r.path("/api/v1//cart/**")
                        .uri("lb://CART-SERVICE"))
                .route("order_service", r -> r.path("/api/v1//order/**")
                        .uri("lb://ORDER-SERVICE"))
                .build();
    }
}
