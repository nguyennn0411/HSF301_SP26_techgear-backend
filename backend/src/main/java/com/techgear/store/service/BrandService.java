package com.techgear.store.service;

import com.techgear.store.entity.Brand;
import com.techgear.store.exception.NotFoundException;
import com.techgear.store.repository.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    // PUBLIC (FE) - thường chỉ lấy active
    public List<Brand> getActiveBrands() {
        return brandRepository.findAll()
                .stream()
                .filter(b -> Boolean.TRUE.equals(b.getIsActive()))
                .toList();
    }

    // ADMIN
    public List<Brand> getAll() {
        return brandRepository.findAll();
    }

    public Brand getById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found"));
    }

    public Brand create(Brand payload) {
        if (payload == null) throw new IllegalArgumentException("Brand payload is required");
        String name = normalizeName(payload.getName());
        if (name.isEmpty()) throw new IllegalArgumentException("Brand name is required");

        // check duplicate name (optional)
        brandRepository.findByNameIgnoreCase(name).ifPresent(x -> {
            throw new IllegalArgumentException("Brand name already exists");
        });

        Brand b = new Brand();
        b.setName(name);
        b.setIsActive(payload.getIsActive() != null ? payload.getIsActive() : true);
        return brandRepository.save(b);
    }

    public Brand update(Long id, Brand payload) {
        Brand b = getById(id);

        if (payload.getName() != null) {
            String newName = normalizeName(payload.getName());
            if (newName.isEmpty()) throw new IllegalArgumentException("Brand name is required");

            brandRepository.findByNameIgnoreCase(newName).ifPresent(exists -> {
                if (!exists.getId().equals(b.getId())) {
                    throw new IllegalArgumentException("Brand name already exists");
                }
            });

            b.setName(newName);
        }

        if (payload.getIsActive() != null) {
            b.setIsActive(payload.getIsActive());
        }

        return brandRepository.save(b);
    }

    // soft disable/enable
    public Brand setActive(Long id, boolean active) {
        Brand b = getById(id);
        b.setIsActive(active);
        return brandRepository.save(b);
    }

    public void delete(Long id) {
        Brand b = getById(id);
        brandRepository.delete(b);
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }
}
