package com.shakir.cart_service.config.webClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${spring.webclient.product.baseurl}")
    private String productBaseUrl;

    @Bean
    public WebClient productWebClient(WebClient.Builder builder){
        return builder
                .baseUrl(productBaseUrl)
                .build();
    }
}

