package com.techgear.store.dto;

import java.util.List;

public class CartResponse {
    private Long cartId;
    private Long userId;
    private Integer totalItems;
    private Double totalAmount;
    private List<CartItemResponse> items;

    public CartResponse() {
    }

    public CartResponse(Long cartId, Long userId, Integer totalItems, Double totalAmount, List<CartItemResponse> items) {
        this.cartId = cartId;
        this.userId = userId;
        this.totalItems = totalItems;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }
}