package com.shakir.order_service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shakir.util_service.constant.OrderStatus;
import com.shakir.util_service.constant.PaymentMethod;
import com.shakir.util_service.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order {
    @Id
    private String id;
    private String customerId;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItemList;
    private BigDecimal orderPrice;
    private BigDecimal tax;
    private BigDecimal discountAmount;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    @Embedded
    private Address ShipingAddress;
    private BigDecimal shippingCharge;
    private BigDecimal grandTotal;


    public Order(String customerId, List<OrderItem> orderItemList, BigDecimal orderPrice, BigDecimal tax,
                 BigDecimal discountAmount, PaymentMethod paymentMethod, PaymentStatus paymentStatus,
                 OrderStatus orderStatus, Address shipingAddress,
                 BigDecimal shippingCharge, BigDecimal grandTotal) {
        this.id = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.orderItemList = orderItemList;
        this.orderPrice = orderPrice;
        this.tax = tax;
        this.discountAmount = discountAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.createdAt = LocalDateTime.now();;
        this.updatedAt = LocalDateTime.now();;
        ShipingAddress = shipingAddress;
        this.shippingCharge = shippingCharge;
        this.grandTotal = grandTotal;
    }

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }


}
