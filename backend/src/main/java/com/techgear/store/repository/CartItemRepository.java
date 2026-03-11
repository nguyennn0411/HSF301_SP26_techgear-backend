package com.techgear.store.repository;

import com.techgear.store.entity.Cart;
import com.techgear.store.entity.CartItem;
import com.techgear.store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}