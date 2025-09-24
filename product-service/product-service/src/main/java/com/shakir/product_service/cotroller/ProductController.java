package com.shakir.product_service.cotroller;

import com.shakir.product_service.dto.AddProductRequest;
import com.shakir.product_service.dto.UpdateProductRequest;
import com.shakir.product_service.service.ProductServiceImpl;
import com.shakir.util_service.ErrorModel;
import com.shakir.util_service.JsonUtils;
import com.shakir.util_service.ResponseWrapper;
import com.shakir.util_service.SuccessModel;
import com.shakir.util_service.product.ProductResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    JsonUtils jsonUtils;


    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(@Valid @RequestBody AddProductRequest productRequest) {
        Optional<ProductResponseDTO> productResponseDTO =
                Optional.ofNullable(productService.addProduct(productRequest));

        ResponseWrapper<ProductResponseDTO> response = new ResponseWrapper<>();

        if (productResponseDTO.isPresent()) {
            SuccessModel successModel = new SuccessModel("Successfully added product", "201");
            response.setData(productResponseDTO.get());
            response.getSuccess().add(successModel);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        } else {
            ErrorModel errorModel = new ErrorModel("Product could not be created", "500");
            response.getErrors().add(errorModel);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @GetMapping("/allProducts")
    public ResponseEntity<?> getAllProducts(){
        ResponseWrapper<List<ProductResponseDTO>> response = new ResponseWrapper<>();
        List<ProductResponseDTO> productResponseDTOList = productService.getAllProduct();
        response.setData(productResponseDTOList);
        return ResponseEntity.status(HttpStatus.OK).body(jsonUtils.responseWithSuccess(response,"OK"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable String id){
        ResponseWrapper<ProductResponseDTO> response = new ResponseWrapper<>();
        ProductResponseDTO productResponseDTO = productService.getProductById(id);
        response.setData(productResponseDTO);
        return ResponseEntity.status(HttpStatus.OK).body(jsonUtils.responseWithSuccess(response,"OK"));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody UpdateProductRequest request){
        ResponseWrapper<ProductResponseDTO> response = new ResponseWrapper<>();
        ProductResponseDTO productResponseDTOList = productService.updateProduct(request);
        response.setData(productResponseDTOList);
        return ResponseEntity.status(HttpStatus.OK).body(jsonUtils.responseWithSuccess(response,"Successfully update the Product"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable String id){
        ResponseWrapper<ProductResponseDTO> response = new ResponseWrapper<>();
        productService.deleteProductById(id);
        return ResponseEntity.status(HttpStatus.OK).body(jsonUtils.responseWithSuccess(response,"Successfully delete the Product"));
    }
}
