package com.shakir.apigateway_service.config;

import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public SwaggerUiConfigParameters swaggerUiConfigParameters(SwaggerUiConfigParameters swaggerUiConfigParameters) {
        // The bean is automatically provided by springdoc
        return swaggerUiConfigParameters;
    }
}

