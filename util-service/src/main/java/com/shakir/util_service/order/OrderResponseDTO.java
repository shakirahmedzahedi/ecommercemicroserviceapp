package com.shakir.util_service.order;

import com.shakir.util_service.constant.OrderStatus;
import com.shakir.util_service.constant.PaymentMethod;
import com.shakir.util_service.constant.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        String id,
        String customerId,
        List<OrderItemResponseDTO> orderItemResponseDTOList,
        BigDecimal orderPrice,
        BigDecimal tax,
        BigDecimal discountAmount,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        OrderStatus orderStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        AddressDTO ShippingAddress,
        BigDecimal shippingCharge,
        BigDecimal grandTotal

) {
}
