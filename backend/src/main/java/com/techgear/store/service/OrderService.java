package com.techgear.store.service;

import com.techgear.store.dto.CreateOrderRequest;
import com.techgear.store.dto.OrderItemResponse;
import com.techgear.store.dto.OrderResponse;
import com.techgear.store.entity.*;
import com.techgear.store.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CouponRepository couponRepository;

    public OrderService(UserRepository userRepository,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        OrderRepository orderRepository,
                        OrderDetailRepository orderDetailRepository,
                        CouponRepository couponRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.couponRepository = couponRepository;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Coupon coupon = null;
        Integer discountPercent = 0;

        if (request != null && request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            String couponCode = request.getCouponCode().trim();

            coupon = couponRepository.findByCode(couponCode)
                    .orElseThrow(() -> new RuntimeException("Coupon not found"));

            LocalDateTime now = LocalDateTime.now();

            if (Boolean.FALSE.equals(coupon.getIsActive())) {
                throw new RuntimeException("Coupon is inactive");
            }

            if (coupon.getStartAt() != null && now.isBefore(coupon.getStartAt())) {
                throw new RuntimeException("Coupon is not started yet");
            }

            if (coupon.getEndAt() != null && now.isAfter(coupon.getEndAt())) {
                throw new RuntimeException("Coupon is expired");
            }

            discountPercent = coupon.getDiscountPercent();
        }

        double subtotal = 0.0;
        List<OrderItemResponse> itemResponses = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product == null) {
                throw new RuntimeException("Product not found in cart");
            }

            if (Boolean.FALSE.equals(product.getIsActive())) {
                throw new RuntimeException("Product is inactive: " + product.getName());
            }

            Integer quantity = cartItem.getQuantity();
            if (quantity == null || quantity <= 0) {
                throw new RuntimeException("Invalid quantity for product: " + product.getName());
            }

            if (product.getStock() < quantity) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            double unitPrice = product.getPrice();
            double lineTotal = unitPrice * quantity;
            subtotal += lineTotal;

            itemResponses.add(new OrderItemResponse(
                    product.getId(),
                    product.getName(),
                    quantity,
                    unitPrice,
                    lineTotal
            ));
        }

        double finalTotal = subtotal;
        if (discountPercent != null && discountPercent > 0) {
            finalTotal = subtotal - (subtotal * discountPercent / 100.0);
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        order.setCoupon(coupon);
        order.setTotal(finalTotal);

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setProduct(product);
            detail.setQuantity(cartItem.getQuantity());
            detail.setUnitPrice(product.getPrice());

            orderDetailRepository.save(detail);

            product.setStock(product.getStock() - cartItem.getQuantity());
        }

        cartItemRepository.deleteAll(cartItems);

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getStatus(),
                savedOrder.getTotal(),
                coupon != null ? coupon.getCode() : null,
                discountPercent,
                savedOrder.getCreatedAt(),
                itemResponses
        );
    }

    public List<OrderResponse> getMyOrders(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
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
                    items
            ));
        }

        return responses;
    }

    public OrderResponse getMyOrderDetail(Long orderId, Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to view this order");
        }

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
                items
        );
    }

    @Transactional
    public String cancelMyOrder(Long orderId, Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to cancel this order");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("Only PENDING orders can be canceled");
        }

        for (OrderDetail detail : order.getDetails()) {
            Product product = detail.getProduct();
            product.setStock(product.getStock() + detail.getQuantity());
        }

        order.setStatus("CANCELED");
        return "Order canceled successfully";
    }
}