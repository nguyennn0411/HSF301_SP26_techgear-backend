package com.techgear.store.service;

import com.techgear.store.dto.WishlistItemDTO;
import com.techgear.store.entity.Product;
import com.techgear.store.entity.User;
import com.techgear.store.entity.Wishlist;
import com.techgear.store.exception.NotFoundException;
import com.techgear.store.repository.ProductRepository;
import com.techgear.store.repository.UserRepository;
import com.techgear.store.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistRepository wishlistRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // LIST: lấy wishlist của user đang đăng nhập
    @Transactional(readOnly = true)
    public List<WishlistItemDTO> getMyWishlist(String email) {
        User u = getUserByEmail(email);
        return wishlistRepository.findByUserId(u.getId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ADD: nếu đã có thì bỏ qua (idempotent)
    @Transactional
    public WishlistItemDTO addToWishlist(String email, Long productId) {
        if (productId == null) throw new IllegalArgumentException("productId is required");

        User u = getUserByEmail(email);

        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        Wishlist w = wishlistRepository.findByUserIdAndProductId(u.getId(), productId)
                .orElseGet(() -> {
                    Wishlist nw = new Wishlist();
                    nw.setUser(u);
                    nw.setProduct(p);
                    return wishlistRepository.save(nw);
                });

        return toDTO(w);
    }

    // REMOVE: nếu không tồn tại -> 404
    @Transactional
    public void removeFromWishlist(String email, Long productId) {
        if (productId == null) throw new IllegalArgumentException("productId is required");

        User u = getUserByEmail(email);

        Wishlist w = wishlistRepository.findByUserIdAndProductId(u.getId(), productId)
                .orElseThrow(() -> new NotFoundException("Wishlist item not found"));

        wishlistRepository.delete(w);
    }

    // ===== helpers =====
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private WishlistItemDTO toDTO(Wishlist w) {
        WishlistItemDTO dto = new WishlistItemDTO();
        dto.setWishlistId(w.getId());
        dto.setCreatedAt(w.getCreatedAt());

        Product p = w.getProduct();
        dto.setProductId(p.getId());
        dto.setProductName(p.getName());
        dto.setPrice(p.getPrice());
        dto.setImageUrl(p.getImageUrl());
        dto.setIsActive(p.getIsActive());
        return dto;
    }
}
