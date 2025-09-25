package com.shakir.order_service.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${spring.webclient.inventory.baseurl}")
    private String inventoryBaseUrl;

    @Value("${spring.webclient.product.baseurl}")
    private String productBaseUrl;

    @Value("${spring.webclient.cart.baseurl}")
    private String cartBaseUrl;

    @Bean(name = "productWebClient")
    public WebClient productWebClient(WebClient.Builder builder){
        return builder
                .baseUrl(productBaseUrl)
                .build();
    }
    @Bean(name = "inventoryWebClient")
    public WebClient inventoryWebClient(WebClient.Builder builder){
        return builder
                .baseUrl(inventoryBaseUrl)
                .build();
    }
    @Bean(name = "cartWebClient")
    public WebClient cartWebClient(WebClient.Builder builder){
        return builder
                .baseUrl(cartBaseUrl)
                .build();
    }

}
