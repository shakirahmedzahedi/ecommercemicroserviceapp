package com.shakir.product_service.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${spring.webclient.inventory.baseurl}")
    private String inventoryBaseUrl;

    @Bean
    public WebClient inventoryWebClient(WebClient.Builder builder){
        return builder
                .baseUrl(inventoryBaseUrl)
                .build();
    }
}
