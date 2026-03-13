package com.techgear.store.service;

import com.techgear.store.dto.PaymentResponse;
import com.techgear.store.entity.Order;
import com.techgear.store.entity.Payment;
import com.techgear.store.entity.User;
import com.techgear.store.repository.OrderRepository;
import com.techgear.store.repository.PaymentRepository;
import com.techgear.store.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository,
                          UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public PaymentResponse payOrder(Long orderId, Authentication authentication) {
        User user = getCurrentUser(authentication);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to pay this order");
        }

        if (!"ONLINE".equalsIgnoreCase(order.getPaymentMethod())) {
            throw new RuntimeException("This order is not an online payment order");
        }

        if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
            throw new RuntimeException("Order already paid");
        }

        Payment payment = paymentRepository.findByOrder(order).orElseGet(() -> {
            Payment p = new Payment();
            p.setOrder(order);
            p.setMethod(order.getPaymentMethod());
            p.setAmount(order.getTotal());
            p.setStatus("PENDING");
            return p;
        });

        // giả lập thanh toán thành công
        payment.setStatus("SUCCESS");
        payment.setTransactionCode("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setPaidAt(LocalDateTime.now());

        order.setPaymentStatus("PAID");
        order.setPaidAt(LocalDateTime.now());
        order.setStatus("PAID");

        Payment saved = paymentRepository.save(payment);

        return new PaymentResponse(
                saved.getId(),
                order.getId(),
                saved.getMethod(),
                saved.getStatus(),
                saved.getTransactionCode(),
                saved.getAmount(),
                "Payment successful"
        );
    }

    @Transactional
    public PaymentResponse failPayment(Long orderId, Authentication authentication) {
        User user = getCurrentUser(authentication);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to update this payment");
        }

        Payment payment = paymentRepository.findByOrder(order).orElseGet(() -> {
            Payment p = new Payment();
            p.setOrder(order);
            p.setMethod(order.getPaymentMethod());
            p.setAmount(order.getTotal());
            return p;
        });

        payment.setStatus("FAILED");
        order.setPaymentStatus("FAILED");
        order.setStatus("PENDING");

        Payment saved = paymentRepository.save(payment);

        return new PaymentResponse(
                saved.getId(),
                order.getId(),
                saved.getMethod(),
                saved.getStatus(),
                saved.getTransactionCode(),
                saved.getAmount(),
                "Payment failed"
        );
    }

    public PaymentResponse getPaymentByOrder(Long orderId, Authentication authentication) {
        User user = getCurrentUser(authentication);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to view this payment");
        }

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return new PaymentResponse(
                payment.getId(),
                order.getId(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getTransactionCode(),
                payment.getAmount(),
                "Payment detail"
        );
    }
}