package com.techgear.store.controller;

import com.techgear.store.dto.WishlistItemDTO;
import com.techgear.store.service.WishlistService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    // GET /api/wishlist
    @GetMapping
    public List<WishlistItemDTO> myWishlist(Authentication auth) {
        return wishlistService.getMyWishlist(auth.getName()); // auth.getName() = email
    }

    // POST /api/wishlist/{productId}
    @PostMapping("/{productId}")
    public WishlistItemDTO add(Authentication auth, @PathVariable Long productId) {
        return wishlistService.addToWishlist(auth.getName(), productId);
    }

    // DELETE /api/wishlist/{productId}
    @DeleteMapping("/{productId}")
    public String remove(Authentication auth, @PathVariable Long productId) {
        wishlistService.removeFromWishlist(auth.getName(), productId);
        return "Removed";
    }
}
