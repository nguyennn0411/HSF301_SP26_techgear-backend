package com.techgear.store.controller;

import com.techgear.store.dto.AddToCartRequest;
import com.techgear.store.dto.CartResponse;
import com.techgear.store.dto.UpdateCartItemRequest;
import com.techgear.store.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getMyCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getMyCart(authentication));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(@RequestBody AddToCartRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.ok(cartService.addToCart(request, authentication));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItem(@PathVariable Long cartItemId,
                                                       @RequestBody UpdateCartItemRequest request,
                                                       Authentication authentication) {
        return ResponseEntity.ok(cartService.updateCartItem(cartItemId, request, authentication));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeCartItem(@PathVariable Long cartItemId,
                                                       Authentication authentication) {
        return ResponseEntity.ok(cartService.removeCartItem(cartItemId, authentication));
    }

    @DeleteMapping
    public ResponseEntity<String> clearMyCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.clearMyCart(authentication));
    }
}