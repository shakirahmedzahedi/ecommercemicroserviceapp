package com.shakir.product_service.service;

import com.shakir.product_service.dto.ProductCreateRequest;
import com.shakir.product_service.entity.Product;
import com.shakir.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public void addProduct(ProductCreateRequest request) {
        Product product = Product.builder()
                .id(UUID.randomUUID().toString())   // generate ID
                .title(request.title())
                .description(request.description())
                .price(new BigDecimal(request.price()))
                .build();

        productRepository.save(product);

    }
}
