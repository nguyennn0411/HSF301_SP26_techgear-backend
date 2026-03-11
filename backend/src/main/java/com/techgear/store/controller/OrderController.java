package com.techgear.store.controller;

import com.techgear.store.dto.CreateOrderRequest;
import com.techgear.store.dto.OrderResponse;
import com.techgear.store.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody(required = false) CreateOrderRequest request,
                                                     Authentication authentication) {
        OrderResponse response = orderService.createOrder(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getMyOrders(authentication));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getMyOrderDetail(@PathVariable Long id,
                                                          Authentication authentication) {
        return ResponseEntity.ok(orderService.getMyOrderDetail(id, authentication));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelMyOrder(@PathVariable Long id,
                                                Authentication authentication) {
        return ResponseEntity.ok(orderService.cancelMyOrder(id, authentication));
    }
}