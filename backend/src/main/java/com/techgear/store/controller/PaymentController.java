package com.techgear.store.controller;

import com.techgear.store.dto.PaymentResponse;
import com.techgear.store.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/orders/{orderId}/pay")
    public ResponseEntity<PaymentResponse> payOrder(@PathVariable Long orderId,
                                                    Authentication authentication) {
        return ResponseEntity.ok(paymentService.payOrder(orderId, authentication));
    }

    @PostMapping("/orders/{orderId}/fail")
    public ResponseEntity<PaymentResponse> failOrderPayment(@PathVariable Long orderId,
                                                            Authentication authentication) {
        return ResponseEntity.ok(paymentService.failPayment(orderId, authentication));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrder(@PathVariable Long orderId,
                                                             Authentication authentication) {
        return ResponseEntity.ok(paymentService.getPaymentByOrder(orderId, authentication));
    }
}