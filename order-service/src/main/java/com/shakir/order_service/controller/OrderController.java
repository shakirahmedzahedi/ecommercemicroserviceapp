package com.shakir.order_service.controller;

import com.shakir.order_service.service.OrderServiceImpl;
import com.shakir.util_service.JsonUtils;
import com.shakir.util_service.ResponseWrapper;
import com.shakir.util_service.order.ConfirmOrderRequest;
import com.shakir.util_service.order.OrderResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    @Autowired
    OrderServiceImpl orderService;
    @Autowired
    JsonUtils jsonUtils;


    @PostMapping("/confirmOrder")
    public ResponseEntity<?> confirmOrder(@Valid @RequestBody ConfirmOrderRequest request){
        ResponseWrapper<OrderResponseDTO> response = new ResponseWrapper<>();
        OrderResponseDTO orderResponseDTO = orderService.confirmOrder(request);
        response.setData(orderResponseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(jsonUtils.responseWithCreated(response,"Order Created successfully!"));
    }

    @GetMapping("/getAllOrders")
    public ResponseEntity<?> getAllOrders(){
        ResponseWrapper<List<OrderResponseDTO>> response = new ResponseWrapper<>();
        List<OrderResponseDTO> orderResponseDTOList = orderService.getAllOrders();
        response.setData(orderResponseDTOList);
        return ResponseEntity.status(HttpStatus.CREATED).body(jsonUtils.responseWithCreated(response,"Order Created successfully!"));
    }
    @GetMapping("/customer")
    public ResponseEntity<?> getAllOrdersByCustomerId(@RequestParam("customerId") String customerId){
        ResponseWrapper<List<OrderResponseDTO>> response = new ResponseWrapper<>();
        List<OrderResponseDTO> orderResponseDTOList = orderService.getAllOrdersByCustomer(customerId);
        response.setData(orderResponseDTOList);
        return ResponseEntity.status(HttpStatus.OK).body(jsonUtils.responseWithSuccess(response,"Order List for the customer"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable("id") String id){
        ResponseWrapper<OrderResponseDTO> response = new ResponseWrapper<>();
        OrderResponseDTO orderResponseDTO = orderService.getOrdersById(id);
        response.setData(orderResponseDTO);
        return ResponseEntity.status(HttpStatus.OK).body(jsonUtils.responseWithSuccess(response,"Order List for the customer"));
    }

}
