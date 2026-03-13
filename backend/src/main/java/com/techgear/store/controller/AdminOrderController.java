package com.techgear.store.controller;

import com.techgear.store.dto.OrderResponse;
import com.techgear.store.dto.UpdateOrderStatusRequest;
import com.techgear.store.dto.UpdatePaymentStatusRequest;
import com.techgear.store.service.AdminOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(adminOrderService.getAllOrders(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderDetail(@PathVariable Long id) {
        return ResponseEntity.ok(adminOrderService.getOrderDetail(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(adminOrderService.updateOrderStatus(id, request.getStatus()));
    }

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<OrderResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestBody UpdatePaymentStatusRequest request
    ) {
        return ResponseEntity.ok(adminOrderService.updatePaymentStatus(id, request.getPaymentStatus()));
    }
}