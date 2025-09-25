package com.shakir.order_service.service;

import com.shakir.util_service.order.ConfirmOrderRequest;
import com.shakir.util_service.order.OrderResponseDTO;

import java.util.List;

public interface OrderService {
    public OrderResponseDTO confirmOrder(ConfirmOrderRequest request);
    public List<OrderResponseDTO> getAllOrders();
    public List<OrderResponseDTO> getAllOrdersByCustomer(String customerId);
}
