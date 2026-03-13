package com.techgear.store.service;

import com.techgear.store.dto.OrderItemResponse;
import com.techgear.store.dto.OrderResponse;
import com.techgear.store.entity.Order;
import com.techgear.store.entity.OrderDetail;
import com.techgear.store.entity.Product;
import com.techgear.store.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminOrderService {

    private final OrderRepository orderRepository;

    public AdminOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public List<OrderResponse> getAllOrders(String status) {
        List<Order> orders;

        if (status != null && !status.trim().isEmpty()) {
            orders = orderRepository.findByStatusOrderByCreatedAtDesc(status.trim().toUpperCase());
        } else {
            orders = orderRepository.findAllByOrderByCreatedAtDesc();
        }

        List<OrderResponse> responses = new ArrayList<>();

        for (Order order : orders) {
            List<OrderItemResponse> items = new ArrayList<>();

            for (OrderDetail detail : order.getDetails()) {
                Product product = detail.getProduct();

                items.add(new OrderItemResponse(
                        product.getId(),
                        product.getName(),
                        detail.getQuantity(),
                        detail.getUnitPrice(),
                        detail.getUnitPrice() * detail.getQuantity()
                ));
            }

            responses.add(new OrderResponse(
                    order.getId(),
                    order.getStatus(),
                    order.getTotal(),
                    order.getCoupon() != null ? order.getCoupon().getCode() : null,
                    order.getCoupon() != null ? order.getCoupon().getDiscountPercent() : null,
                    order.getCreatedAt(),
                    order.getPaymentMethod(),
                    order.getPaymentStatus(),
                    items
            ));
        }

        return responses;
    }

    @Transactional
    public OrderResponse getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderItemResponse> items = new ArrayList<>();

        for (OrderDetail detail : order.getDetails()) {
            Product product = detail.getProduct();

            items.add(new OrderItemResponse(
                    product.getId(),
                    product.getName(),
                    detail.getQuantity(),
                    detail.getUnitPrice(),
                    detail.getUnitPrice() * detail.getQuantity()
            ));
        }

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotal(),
                order.getCoupon() != null ? order.getCoupon().getCode() : null,
                order.getCoupon() != null ? order.getCoupon().getDiscountPercent() : null,
                order.getCreatedAt(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                items
        );
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String status = newStatus == null ? "" : newStatus.trim().toUpperCase();

        if (!status.equals("PENDING") &&
                !status.equals("PAID") &&
                !status.equals("SHIPPED") &&
                !status.equals("CANCELED")) {
            throw new IllegalArgumentException("Invalid order status");
        }

        order.setStatus(status);

        return mapOrderResponse(order);
    }

    @Transactional
    public OrderResponse updatePaymentStatus(Long orderId, String newPaymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String paymentStatus = newPaymentStatus == null ? "" : newPaymentStatus.trim().toUpperCase();

        if (!paymentStatus.equals("UNPAID") &&
                !paymentStatus.equals("PENDING") &&
                !paymentStatus.equals("PAID") &&
                !paymentStatus.equals("FAILED")) {
            throw new IllegalArgumentException("Invalid payment status");
        }

        order.setPaymentStatus(paymentStatus);

        if ("PAID".equals(paymentStatus)) {
            order.setPaidAt(LocalDateTime.now());
            if ("PENDING".equals(order.getStatus())) {
                order.setStatus("PAID");
            }
        }

        return mapOrderResponse(order);
    }

    private OrderResponse mapOrderResponse(Order order) {
        List<OrderItemResponse> items = new ArrayList<>();

        for (OrderDetail detail : order.getDetails()) {
            Product product = detail.getProduct();

            items.add(new OrderItemResponse(
                    product.getId(),
                    product.getName(),
                    detail.getQuantity(),
                    detail.getUnitPrice(),
                    detail.getUnitPrice() * detail.getQuantity()
            ));
        }

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotal(),
                order.getCoupon() != null ? order.getCoupon().getCode() : null,
                order.getCoupon() != null ? order.getCoupon().getDiscountPercent() : null,
                order.getCreatedAt(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                items
        );
    }
}