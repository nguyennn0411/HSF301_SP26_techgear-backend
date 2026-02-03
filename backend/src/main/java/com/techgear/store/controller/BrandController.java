package com.techgear.store.controller;

import com.techgear.store.entity.Brand;
import com.techgear.store.service.BrandService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    // ===== PUBLIC =====
    @GetMapping("/active")
    public List<Brand> active() {
        return brandService.getActiveBrands();
    }

    // ===== ADMIN =====
    @PreAuthorize("hasAnyAuthority('ROLE_OWNER','ROLE_STAFF')")
    @GetMapping
    public List<Brand> all() {
        return brandService.getAll();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_OWNER','ROLE_STAFF')")
    @GetMapping("/{id}")
    public Brand get(@PathVariable Long id) {
        return brandService.getById(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_OWNER','ROLE_STAFF')")
    @PostMapping
    public Brand create(@RequestBody Brand brand) {
        return brandService.create(brand);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_OWNER','ROLE_STAFF')")
    @PutMapping("/{id}")
    public Brand update(@PathVariable Long id, @RequestBody Brand brand) {
        return brandService.update(id, brand);
    }

    // soft enable/disable
    @PreAuthorize("hasAnyAuthority('ROLE_OWNER','ROLE_STAFF')")
    @PutMapping("/{id}/active")
    public Brand setActive(@PathVariable Long id, @RequestParam boolean value) {
        return brandService.setActive(id, value);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_OWNER','ROLE_STAFF')")
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        brandService.delete(id);
        return "Deleted";
    }
}
