package com.shakir.order_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderItem {
    @Id
    private String Id;
    private String productId;
    private long quantity;
    private BigDecimal total;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;

}
