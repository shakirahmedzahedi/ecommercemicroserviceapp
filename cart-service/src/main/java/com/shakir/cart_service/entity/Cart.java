package com.shakir.cart_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cart")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Cart {
    @Id
    private String id;
    private String customerId;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;
    private BigDecimal totalCartPrice;

    public Cart(String customerId, List<CartItem> cartItemList) {
        this.id = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.items = cartItemList;
        this.totalCartPrice = calculateTotalPrice(cartItemList);
    }

    private BigDecimal calculateTotalPrice(List<CartItem> cartItemList){
        if (cartItemList == null || cartItemList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return cartItemList.stream()
                .map(CartItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", totalCartPrice=" + totalCartPrice +
                '}';
    }
}
