package com.shakir.product_service.cotroller;

import com.shakir.product_service.dto.ProductCreateRequest;
import com.shakir.product_service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

     @Autowired
     private ProductService  productService;


    @PostMapping("/addProduct")
    public String addProduct(@Valid @RequestBody ProductCreateRequest productRequest){
        productService.addProduct(productRequest);
        return "successfully add product";
    }
}
