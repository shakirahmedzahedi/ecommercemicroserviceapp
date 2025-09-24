package com.shakir.cart_service.service;

import com.shakir.util_service.cart.AddToCartRequest;
import com.shakir.util_service.cart.CartResponseDTO;
import com.shakir.util_service.cart.RemoveFromCartRequest;

import java.util.List;

public interface CartService {
    public CartResponseDTO addToCart(AddToCartRequest request);
    public CartResponseDTO removeFromCart(RemoveFromCartRequest request);
    public CartResponseDTO getCartByUserId(String userId);
    public List<CartResponseDTO> getAllCarts();
}
