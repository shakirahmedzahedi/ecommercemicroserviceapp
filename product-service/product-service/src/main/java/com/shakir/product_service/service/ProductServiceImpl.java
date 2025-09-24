package com.shakir.product_service.service;

import com.shakir.product_service.dto.AddProductRequest;
import com.shakir.product_service.dto.UpdateProductRequest;
import com.shakir.product_service.entity.Product;
import com.shakir.product_service.repository.ProductRepository;
import com.shakir.product_service.utils.ProductMapper;
import com.shakir.util_service.Category;
import com.shakir.util_service.ResponseWrapper;
import com.shakir.util_service.Tags;
import com.shakir.util_service.exceptions.EntityNotFoundException;
import com.shakir.util_service.inventory.AddStockRequest;
import com.shakir.util_service.inventory.InventoryResponseDTO;
import com.shakir.util_service.product.ProductResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.shakir.product_service.utils.ProductMapper.extractProductFromRequest;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;


    @Override
    public ProductResponseDTO addProduct(AddProductRequest request) {
        Product product = extractProductFromRequest(request);
        productRepository.save(product);

        AddStockRequest addStockRequest = new AddStockRequest(product.getId(), Long.parseLong(request.stock()));

        ResponseWrapper<InventoryResponseDTO> response = productMapper.mapToInventoryDTOForAddStock(request, addStockRequest, product);

        return productMapper.toProductResponseDTO(product, response.getData().quantity());

    }


    @Override
    public List<ProductResponseDTO> getAllProduct() {
        List<Product> productList = productRepository.findAll();

        return productList.stream().map(product -> productMapper.mapToInventoryDTOForGetStockByProductId(product)).toList();
    }


    @Override
    public ProductResponseDTO getProductById(String id) {
        Product product = productRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Product not found"));
        ProductResponseDTO productResponseDTO = productMapper.mapToInventoryDTOForGetStockByProductId(product);
        return  productMapper.toProductResponseDTO(product, productResponseDTO.stock());
    }
    @Override
    public void deleteProductById(String id) {
        productRepository.deleteById(id);
        productMapper.mapToInventoryDTOForDeleteStock(id);

    }


    @Override
    public ProductResponseDTO updateProduct(UpdateProductRequest request) {
        Product product = productRepository.findById(request.id()).orElseThrow(()-> new RuntimeException("Product not found"));
        product.setTitle(request.title());
        product.setDescription(request.description());
        product.setAdditionalInfo(request.additionalInfo());
        product.setExtraInfo(request.extraInfo());
        product.setPrice(new BigDecimal(request.price()));
        product.setDiscountPercentage(Double.parseDouble(request.discountPercentage()));
        product.setCategory(Category.valueOf(request.category()));
        product.setTag(Tags.valueOf(request.tag()));
        product.setBrand(request.brand());
        product.setSize(request.size());
        product.setWeight(Long.parseLong(request.weight()));
        product.setThumbnail(request.thumbnail());
        product.setImagesList(request.imagesList());

        productRepository.save(product);

        ProductResponseDTO productResponseDTO = productMapper.mapToInventoryDTOForUpdateStock(product, Long.parseLong(request.stock()));

        return productMapper.toProductResponseDTO(product,productResponseDTO.stock());
    }


}
