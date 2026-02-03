package com.techgear.store.controller;

import com.techgear.store.entity.Coupon;
import com.techgear.store.service.CouponService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    // ===== PUBLIC: validate coupon by code (for checkout) =====
    // Bạn đã permitAll /api/products..., còn /api/coupons/** đang authenticated trong SecurityConfig.
    // => cần update SecurityConfig để permit /api/coupons/validate (mình ghi ở cuối).
    @PostMapping("/validate")
    public Coupon validate(@RequestParam String code) {
        return couponService.validateByCode(code);
    }

    // ===== ADMIN: CRUD =====

    @PreAuthorize("hasAnyAuthority('ROLE_OWNER','ROLE_STAFF')")
    @GetMapping
    public List<Coupon> getAll() {
        return couponService.getAll();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_OWNER','ROLE_STAFF')")
    @GetMapping("/{id}")
    public Coupon getById(@PathVariable Long id) {
        return couponService.getById(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_OWNER','ROLE_STAFF')")
    @PostMapping
    public Coupon create(@RequestBody Coupon coupon) {
        return couponService.create(coupon);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_OWNER','ROLE_STAFF')")
    @PutMapping("/{id}")
    public Coupon update(@PathVariable Long id, @RequestBody Coupon coupon) {
        return couponService.update(id, coupon);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_OWNER','ROLE_STAFF')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        couponService.delete(id);
    }
}
