package com.shakir.cart_service.controller;

import com.shakir.cart_service.service.CartServiceImpl;
import com.shakir.util_service.JsonUtils;
import com.shakir.util_service.ResponseWrapper;
import com.shakir.util_service.cart.AddToCartRequest;
import com.shakir.util_service.cart.CartResponseDTO;
import com.shakir.util_service.cart.RemoveFromCartRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/cart")
public class CartController {

    @Autowired
    CartServiceImpl cartServiceImpl;
    @Autowired
    JsonUtils jsonUtils;

    @PostMapping("/addToCart")
    public ResponseEntity<?> addToCart(@Valid @RequestBody AddToCartRequest request){
        ResponseWrapper<CartResponseDTO> response = new ResponseWrapper<>();
        CartResponseDTO cartResponseDTO = cartServiceImpl.addToCart(request);
        response.setData(cartResponseDTO);
        return ResponseEntity.status(HttpStatus.OK).body(jsonUtils.responseWithSuccess(response,"OK"));
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<?> removeFromCart(@Valid @RequestBody RemoveFromCartRequest request){
        ResponseWrapper<CartResponseDTO> response = new ResponseWrapper<>();
        CartResponseDTO cartResponseDTO = cartServiceImpl.removeFromCart(request);
        response.setData(cartResponseDTO);
        return ResponseEntity.status(HttpStatus.OK).body(jsonUtils.responseWithSuccess(response,"OK"));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCartByCustomerId(@PathVariable("customerId") String customerId){
        ResponseWrapper<CartResponseDTO> response = new ResponseWrapper<>();
        CartResponseDTO cartResponseDTO = cartServiceImpl.getCartByCustomerId(customerId);
        response.setData(cartResponseDTO);
        return ResponseEntity.status(HttpStatus.OK).body(jsonUtils.responseWithSuccess(response,"OK"));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteCartByCustomerId(@PathVariable("customerId") String customerId){
        ResponseWrapper<CartResponseDTO> response = new ResponseWrapper<>();
        cartServiceImpl.deleteCartByCustomerId(customerId);
        return ResponseEntity.status(HttpStatus.OK).body(jsonUtils.responseWithSuccess(response,"SuccessFully delete the Cart"));
    }

}
