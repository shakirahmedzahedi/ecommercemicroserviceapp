package com.shakir.cart_service.service;

import com.shakir.cart_service.entity.Cart;
import com.shakir.cart_service.entity.CartItem;
import com.shakir.cart_service.repository.CartItemRepository;
import com.shakir.cart_service.repository.CartRepository;
import com.shakir.util_service.ResponseWrapper;
import com.shakir.util_service.cart.AddToCartRequest;
import com.shakir.util_service.cart.CartItemResponseDTO;
import com.shakir.util_service.cart.CartResponseDTO;
import com.shakir.util_service.cart.RemoveFromCartRequest;
import com.shakir.util_service.exceptions.EntityNotFoundException;
import com.shakir.util_service.product.ProductResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService{
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    WebClient productWebClient;

    @Override
    public CartResponseDTO addToCart(AddToCartRequest request) {

       ProductResponseDTO productResponseDTO = getProductResponseDTO(request.productId());
       Cart cart = cartRepository.findByCustomerId(request.customerId()).orElseGet(()->
       {
           Cart newCart = new Cart(request.customerId(), new ArrayList<>());
           cartRepository.save(newCart);
           return newCart;
       });

      Optional<CartItem> exitingCartItemOpt= cart.getItems().stream()
              .filter(cartItem -> Objects.equals(cartItem.getProductId(), request.productId()))
              .findFirst();

      if(exitingCartItemOpt.isPresent()){
          CartItem cartItem = exitingCartItemOpt.get();
          cartItem.setQuantity(cartItem.getQuantity()+ Long.parseLong(request.quantity()));
          cartItem.setTotal(productResponseDTO.price().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
          cartItemRepository.save(cartItem);
      }
      else {
          BigDecimal total = BigDecimal.valueOf(Long.parseLong(request.quantity())).multiply(productResponseDTO.price());
          CartItem cartItem = new CartItem(request.productId(),Long.parseLong(request.quantity()), total,cart);
          cart.getItems().add(cartItem);
          cartRepository.save(cart);
      }
        BigDecimal newTotal = cart.getItems().stream()
                .map(CartItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalCartPrice(newTotal);
        cartRepository.save(cart);
        return toCartResponseDTO(cart);
    }

    private CartResponseDTO toCartResponseDTO(Cart cart) {
        return new CartResponseDTO(
                cart.getId(),
                cart.getCustomerId(),
                cart.getItems().stream().map(cartItem -> toCartItemResponseDTO(cartItem, getProductResponseDTO(cartItem.getProductId()))).toList(),
                cart.getTotalCartPrice()
        );
    }

    private CartItemResponseDTO toCartItemResponseDTO(CartItem cartItem, ProductResponseDTO productResponseDTO){
        return new CartItemResponseDTO(
                cartItem.getId(),
                productResponseDTO,
                cartItem.getQuantity(),
                cartItem.getTotal()
        );
    }

    private ProductResponseDTO getProductResponseDTO(String productId){
        ResponseWrapper<ProductResponseDTO> productResponse = productWebClient.get()
                .uri("/{id}",productId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseWrapper<ProductResponseDTO>>() {
                })
                .block();

        return  Optional.ofNullable(productResponse)
                .map(ResponseWrapper::getData)
                .orElseThrow(() -> new RuntimeException("Product data not found"));
    }

    @Override
    public CartResponseDTO getCartByUserId(String userId) {
        return null;
    }

    @Override
    public List<CartResponseDTO> getAllCarts() {
        return null;
    }
    @Override
    public CartResponseDTO removeFromCart(RemoveFromCartRequest request) {
        Cart cart = cartRepository.findByCustomerId(request.customerId())
                .orElseThrow(()-> new EntityNotFoundException("Cart not found"));
        ProductResponseDTO productResponseDTO = getProductResponseDTO(request.productId());

        Optional<CartItem> exitingCartItemOpt= cart.getItems().stream()
                .filter(cartItem -> Objects.equals(cartItem.getProductId(), request.productId()))
                .findFirst();

        if(exitingCartItemOpt.isPresent()){
            CartItem cartItem = exitingCartItemOpt.get();
            if(cartItem.getQuantity() > 1){
                cartItem.setQuantity(cartItem.getQuantity()- Long.parseLong(request.quantity()));
                cartItem.setTotal(productResponseDTO.price().multiply(new BigDecimal(cartItem.getQuantity())));
                cartItemRepository.save(cartItem);
            }
            else {
                cart.getItems().remove(cartItem);
                cartItemRepository.delete(cartItem);
            }
        }
        else {
            throw new EntityNotFoundException("Something not found");
        }
        if (cart.getItems().isEmpty()) {
            cartRepository.delete(cart);
            return null; // or return a special DTO saying "Cart deleted"
        }

        BigDecimal newTotal = cart.getItems().stream()
                .map(CartItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalCartPrice(newTotal);
        cartRepository.save(cart);

        return toCartResponseDTO(cart);

    }

    public CartResponseDTO getCartByCustomerId(String customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(()-> new EntityNotFoundException("Cart not found"));

        return toCartResponseDTO(cart);
    }

    public void deleteCartByCustomerId(String customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(()-> new EntityNotFoundException("Cart not found!"));
        cartRepository.delete(cart);

    }
}
