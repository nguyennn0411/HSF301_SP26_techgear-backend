package com.techgear.store.controller;

import com.techgear.store.entity.Cart;
import com.techgear.store.service.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;
    public CartController(CartService cartService) { this.cartService = cartService; }

    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        return cartService.getOrCreateCart(userId);
    }

    @PostMapping("/{userId}/items/{productId}")
    public Cart add(@PathVariable Long userId, @PathVariable Long productId,
                    @RequestParam(defaultValue = "1") int qty) {
        return cartService.addItem(userId, productId, qty);
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public void remove(@PathVariable Long userId, @PathVariable Long productId) {
        cartService.removeItem(userId, productId);
    }

    @DeleteMapping("/{userId}/clear")
    public void clear(@PathVariable Long userId) {
        cartService.clear(userId);
    }
}
