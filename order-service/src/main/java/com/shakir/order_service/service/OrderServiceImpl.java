package com.shakir.order_service.service;

import com.shakir.order_service.entity.Address;
import com.shakir.order_service.entity.Order;
import com.shakir.order_service.entity.OrderItem;
import com.shakir.order_service.repository.OrderItemRepository;
import com.shakir.order_service.repository.OrderRepository;
import com.shakir.util_service.ResponseWrapper;
import com.shakir.util_service.constant.OrderStatus;
import com.shakir.util_service.constant.PaymentMethod;
import com.shakir.util_service.constant.PaymentStatus;
import com.shakir.util_service.exceptions.EntityNotFoundException;
import com.shakir.util_service.exceptions.IllegalStateException;
import com.shakir.util_service.inventory.InventoryResponseDTO;
import com.shakir.util_service.inventory.UpdateStockRequest;
import com.shakir.util_service.order.*;
import com.shakir.util_service.product.ProductResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    @Qualifier("productWebClient")
    WebClient productWebClient;
    @Autowired
    @Qualifier("inventoryWebClient")
    WebClient inventoryWebClient;

    @Override
    public OrderResponseDTO confirmOrder(ConfirmOrderRequest request) {

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal orderPrice = BigDecimal.ZERO;
        for(OrderItemDTORequest orderItemDTORequest : request.orderItemDTORequestList()){
            ProductResponseDTO productResponseDTO = getProductResponseDTO(orderItemDTORequest.productId());
            InventoryResponseDTO inventoryResponseDTO = getInventoryResponseDTO(orderItemDTORequest.productId());

            if(Long.parseLong(orderItemDTORequest.quantity()) > inventoryResponseDTO.quantity()){
                throw new IllegalStateException("Not enough Stock for Product");
            }
            else{
                InventoryResponseDTO updatedInventoryResponse = updateInventory(orderItemDTORequest.productId(),
                        (productResponseDTO.stock()-Long.parseLong(orderItemDTORequest.quantity())));
            }

            BigDecimal itemTotalPrice = productResponseDTO.price().multiply(BigDecimal.valueOf(Long.parseLong(orderItemDTORequest.quantity())));
            orderPrice = orderPrice.add(itemTotalPrice);
            OrderItem orderItem = new OrderItem(
                    UUID.randomUUID().toString(),
                    productResponseDTO.id(),
                    Long.parseLong(orderItemDTORequest.quantity()),
                    itemTotalPrice,
                    null
            );
            orderItems.add(orderItem);
        }
        BigDecimal tax = orderPrice.multiply(new BigDecimal("0.25"));
        BigDecimal shippingCharge = new BigDecimal(Long.parseLong(request.shippingCharge()));
        BigDecimal discount = new BigDecimal(Long.parseLong(request.discountAmount()));

        BigDecimal grandTotal = orderPrice.add(tax).add(shippingCharge).subtract(discount);

        Order order = new Order(
                request.customerId(),
                orderItems,
                orderPrice,
                tax,
                discount,
                PaymentMethod.CONFIRM,
                PaymentStatus.PENDING,
                OrderStatus.PENDING,
                fromAddressDTO(request.address()),
                shippingCharge,
                grandTotal
        );

        orderItems.forEach(orderItem -> {
                    orderItem.setOrder(order);
                });


        orderRepository.save(order);

        return toOrderResponseDTO(order);
    }
    private Address fromAddressDTO(AddressDTO addressDTO){
        return new Address(
                addressDTO.street(),
                addressDTO.city(),
                addressDTO.state(),
                addressDTO.zip(),
                addressDTO.country()
        );


    }

    private  OrderResponseDTO toOrderResponseDTO(Order order){
        return new OrderResponseDTO(
                order.getId(),
                order.getCustomerId(),
                toOrderItemsDTO(order.getOrderItemList()),
                order.getOrderPrice(),
                order.getTax(),
                order.getDiscountAmount(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getOrderStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                toAddressDTO(order.getShipingAddress()),
                order.getShippingCharge(),
                order.getGrandTotal()

        );

    }

    private List<OrderItemResponseDTO> toOrderItemsDTO(List<OrderItem> orderItemList) {
        return orderItemList.stream().map(orderItem -> new OrderItemResponseDTO(
                orderItem.getId(),
                getProductResponseDTO(orderItem.getProductId()),
                orderItem.getQuantity(),
                orderItem.getTotal()
        )).toList();
    }

    private ProductResponseDTO getProductResponseDTO(String productId) {
        ResponseWrapper<ProductResponseDTO> productResponse = productWebClient.get()
                .uri("/{id}",productId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseWrapper<ProductResponseDTO>>() {
                })
                .block();

        return  Optional.ofNullable(productResponse)
                .map(ResponseWrapper::getData)
                .orElseThrow(() -> new EntityNotFoundException("Product data not found"));
    }

    private InventoryResponseDTO getInventoryResponseDTO(String productId) {
        ResponseWrapper<InventoryResponseDTO> inventoryResponse = inventoryWebClient.get()
                .uri("/{id}",productId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseWrapper<InventoryResponseDTO>>() {
                })
                .block();

        return  Optional.ofNullable(inventoryResponse)
                .map(ResponseWrapper::getData)
                .orElseThrow(() -> new EntityNotFoundException("Inventory data not found"));
    }

    private InventoryResponseDTO updateInventory(String productId, Long quantity) {
        ResponseWrapper<InventoryResponseDTO> inventoryResponse = inventoryWebClient.put()
                .uri("/updateStock")
                .bodyValue(new UpdateStockRequest(productId,quantity))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseWrapper<InventoryResponseDTO>>() {
                })
                .block();

        return  Optional.ofNullable(inventoryResponse)
                .map(ResponseWrapper::getData)
                .orElseThrow(() -> new EntityNotFoundException("Inventory data not found"));
    }

    private AddressDTO toAddressDTO(Address shipingAddress) {
        return new AddressDTO(
                shipingAddress.getStreet(),
                shipingAddress.getCity(),
                shipingAddress.getState(),
                shipingAddress.getZip(),
                shipingAddress.getCountry()
        );
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orderList = orderRepository.findAll();
        return orderList.stream().map(this::toOrderResponseDTO).toList();
    }

    @Override
    public List<OrderResponseDTO> getAllOrdersByCustomer(String customerId) {
        List<Order> orderList = orderRepository.findAllByCustomerId(customerId);
        return orderList.stream().map(this::toOrderResponseDTO).toList();
    }

    public OrderResponseDTO getOrdersById(String id) {
        Order order = orderRepository.findById(id).orElseThrow(()->
                new EntityNotFoundException("Order not exist!"));
        return  toOrderResponseDTO(order);
    }
}
