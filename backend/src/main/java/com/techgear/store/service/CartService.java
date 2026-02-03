package com.techgear.store.service;

import com.techgear.store.entity.Cart;
import com.techgear.store.entity.CartItem;
import com.techgear.store.entity.Product;
import com.techgear.store.entity.User;
import com.techgear.store.exception.NotFoundException;
import com.techgear.store.repository.CartItemRepository;
import com.techgear.store.repository.CartRepository;
import com.techgear.store.repository.ProductRepository;
import com.techgear.store.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User u = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
            Cart c = new Cart();
            c.setUser(u);
            return cartRepository.save(c);
        });
    }

    @Transactional
    public Cart addItem(Long userId, Long productId, int qty) {
        if (qty <= 0) throw new IllegalArgumentException("Quantity must be > 0");
        Cart cart = getOrCreateCart(userId);
        Product p = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setCart(cart);
                    ci.setProduct(p);
                    ci.setQuantity(0);
                    return ci;
                });

        item.setQuantity(item.getQuantity() + qty);
        cartItemRepository.save(item);
        return cartRepository.findById(cart.getId()).orElseThrow();
    }

    @Transactional
    public void removeItem(Long userId, Long productId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new NotFoundException("Cart item not found"));
        cartItemRepository.delete(item);
    }

    @Transactional
    public void clear(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
