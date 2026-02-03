package com.techgear.store.service;

import com.techgear.store.entity.Coupon;
import com.techgear.store.exception.NotFoundException;
import com.techgear.store.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    // ===== CRUD (Admin) =====

    public List<Coupon> getAll() {
        return couponRepository.findAll();
    }

    public Coupon getById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Coupon not found"));
    }

    public Coupon create(Coupon c) {
        normalize(c);
        validateCouponFields(c);
        // code unique check
        couponRepository.findByCode(c.getCode()).ifPresent(x -> {
            throw new IllegalArgumentException("Coupon code already exists");
        });
        return couponRepository.save(c);
    }

    public Coupon update(Long id, Coupon payload) {
        Coupon c = getById(id);

        if (payload.getCode() != null && !payload.getCode().trim().isEmpty()) {
            String newCode = payload.getCode().trim().toUpperCase();
            couponRepository.findByCode(newCode).ifPresent(exists -> {
                if (!exists.getId().equals(c.getId())) {
                    throw new IllegalArgumentException("Coupon code already exists");
                }
            });
            c.setCode(newCode);
        }

        if (payload.getDiscountPercent() != null) c.setDiscountPercent(payload.getDiscountPercent());
        if (payload.getStartAt() != null) c.setStartAt(payload.getStartAt());
        if (payload.getEndAt() != null) c.setEndAt(payload.getEndAt());
        if (payload.getIsActive() != null) c.setIsActive(payload.getIsActive());

        normalize(c);
        validateCouponFields(c);
        return couponRepository.save(c);
    }

    public void delete(Long id) {
        Coupon c = getById(id);
        couponRepository.delete(c);
    }

    // ===== Validate (Public / Checkout) =====

    /**
     * Validate coupon code at "now".
     * - Code must exist
     * - isActive must be true
     * - now must be within [startAt, endAt] if provided
     */
    public Coupon validateByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Coupon code is required");
        }

        String normalized = code.trim().toUpperCase();

        Coupon c = couponRepository.findByCode(normalized)
                .orElseThrow(() -> new NotFoundException("Coupon not found"));

        if (!Boolean.TRUE.equals(c.getIsActive())) {
            throw new IllegalArgumentException("Coupon is inactive");
        }

        LocalDateTime now = LocalDateTime.now();

        // startAt optional
        if (c.getStartAt() != null && now.isBefore(c.getStartAt())) {
            throw new IllegalArgumentException("Coupon is not started yet");
        }

        // endAt optional
        if (c.getEndAt() != null && now.isAfter(c.getEndAt())) {
            throw new IllegalArgumentException("Coupon is expired");
        }

        // discount sanity
        if (c.getDiscountPercent() == null || c.getDiscountPercent() < 1 || c.getDiscountPercent() > 100) {
            throw new IllegalArgumentException("Coupon discount percent is invalid");
        }

        return c;
    }

    // ===== Helpers =====

    private void normalize(Coupon c) {
        if (c.getCode() != null) c.setCode(c.getCode().trim().toUpperCase());
        if (c.getIsActive() == null) c.setIsActive(true);
    }

    private void validateCouponFields(Coupon c) {
        if (c.getCode() == null || c.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Coupon code is required");
        }
        if (c.getDiscountPercent() == null || c.getDiscountPercent() < 1 || c.getDiscountPercent() > 100) {
            throw new IllegalArgumentException("Discount percent must be 1..100");
        }
        if (c.getStartAt() != null && c.getEndAt() != null && c.getEndAt().isBefore(c.getStartAt())) {
            throw new IllegalArgumentException("Invalid time range: endAt must be after startAt");
        }
    }
}
