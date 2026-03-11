package com.techgear.store.service;

import com.techgear.store.dto.AddToCartRequest;
import com.techgear.store.dto.CartItemResponse;
import com.techgear.store.dto.CartResponse;
import com.techgear.store.dto.UpdateCartItemRequest;
import com.techgear.store.entity.Cart;
import com.techgear.store.entity.CartItem;
import com.techgear.store.entity.Product;
import com.techgear.store.entity.User;
import com.techgear.store.repository.CartItemRepository;
import com.techgear.store.repository.CartRepository;
import com.techgear.store.repository.ProductRepository;
import com.techgear.store.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    private CartResponse mapCartResponse(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        List<CartItemResponse> itemResponses = new ArrayList<>();

        int totalItems = 0;
        double totalAmount = 0.0;

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            double lineTotal = product.getPrice() * item.getQuantity();

            totalItems += item.getQuantity();
            totalAmount += lineTotal;

            itemResponses.add(new CartItemResponse(
                    item.getId(),
                    product.getId(),
                    product.getName(),
                    product.getImageUrl(),
                    product.getPrice(),
                    item.getQuantity(),
                    lineTotal,
                    product.getStock()
            ));
        }

        return new CartResponse(
                cart.getId(),
                cart.getUser().getId(),
                totalItems,
                totalAmount,
                itemResponses
        );
    }

    @Transactional
    public CartResponse getMyCart(Authentication authentication) {
        User user = getCurrentUser(authentication);
        Cart cart = getOrCreateCart(user);
        return mapCartResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(AddToCartRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Cart cart = getOrCreateCart(user);

        if (request.getProductId() == null) {
            throw new RuntimeException("Product id is required");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (Boolean.FALSE.equals(product.getIsActive())) {
            throw new RuntimeException("Product is inactive");
        }

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product).orElse(null);

        int newQuantity = request.getQuantity();
        if (cartItem != null) {
            newQuantity = cartItem.getQuantity() + request.getQuantity();
        }

        if (product.getStock() < newQuantity) {
            throw new RuntimeException("Not enough stock for product: " + product.getName());
        }

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
        } else {
            cartItem.setQuantity(newQuantity);
        }

        cartItemRepository.save(cartItem);
        return mapCartResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItem(Long cartItemId, UpdateCartItemRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("You are not allowed to update this cart item");
        }

        if (request.getQuantity() == null) {
            throw new RuntimeException("Quantity is required");
        }

        if (request.getQuantity() <= 0) {
            cartItemRepository.delete(cartItem);
            return mapCartResponse(cart);
        }

        Product product = cartItem.getProduct();
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Not enough stock for product: " + product.getName());
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        return mapCartResponse(cart);
    }

    @Transactional
    public CartResponse removeCartItem(Long cartItemId, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("You are not allowed to delete this cart item");
        }

        cartItemRepository.delete(cartItem);
        return mapCartResponse(cart);
    }

    @Transactional
    public String clearMyCart(Authentication authentication) {
        User user = getCurrentUser(authentication);
        Cart cart = getOrCreateCart(user);

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        cartItemRepository.deleteAll(cartItems);

        return "Cart cleared successfully";
    }
}