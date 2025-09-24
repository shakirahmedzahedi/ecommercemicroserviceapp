package com.shakir.product_service.service;

import com.shakir.product_service.dto.AddProductRequest;
import com.shakir.product_service.dto.UpdateProductRequest;
import com.shakir.util_service.product.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    ProductResponseDTO addProduct(AddProductRequest request);

    List<ProductResponseDTO> getAllProduct();

    ProductResponseDTO getProductById(String id);

    void deleteProductById(String id);

    ProductResponseDTO updateProduct(UpdateProductRequest request);
}
