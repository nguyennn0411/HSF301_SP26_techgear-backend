package com.techgear.store.service;

import com.techgear.store.dto.ProductFilterDTO;
import com.techgear.store.entity.Product;
import com.techgear.store.exception.NotFoundException;
import com.techgear.store.repository.ProductRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAll(ProductFilterDTO f) {
        Specification<Product> spec = Specification.where((Specification<Product>)null);

        if (f != null) {
            if (f.getCategoryId() != null) {
                spec = spec.and((root, q, cb) -> cb.equal(root.get("category").get("id"), f.getCategoryId()));
            }
            if (f.getBrandId() != null) {
                spec = spec.and((root, q, cb) -> cb.equal(root.get("brand").get("id"), f.getBrandId()));
            }
            if (f.getMinPrice() != null) {
                spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("price"), f.getMinPrice()));
            }
            if (f.getMaxPrice() != null) {
                spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("price"), f.getMaxPrice()));
            }
            if (f.getKeyword() != null && !f.getKeyword().trim().isEmpty()) {
                String kw = "%" + f.getKeyword().trim().toLowerCase() + "%";
                spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("name")), kw));
            }
            if (Boolean.TRUE.equals(f.getActiveOnly())) {
                spec = spec.and((root, q, cb) -> cb.isTrue(root.get("isActive")));
            }
        }

        return productRepository.findAll(spec);
    }

    public Product getById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public Product create(Product p) {
        if (p.getPrice() == null || p.getPrice() <= 0) throw new IllegalArgumentException("Price must be > 0");
        if (p.getStock() == null || p.getStock() < 0) throw new IllegalArgumentException("Stock must be >= 0");
        return productRepository.save(p);
    }

    public Product update(Long id, Product payload) {
        Product p = getById(id);
        p.setName(payload.getName());
        p.setDescription(payload.getDescription());
        p.setPrice(payload.getPrice());
        p.setStock(payload.getStock());
        p.setColor(payload.getColor());
        p.setSize(payload.getSize());
        p.setImageUrl(payload.getImageUrl());
        p.setIsActive(payload.getIsActive());
        p.setCategory(payload.getCategory());
        p.setBrand(payload.getBrand());
        return productRepository.save(p);
    }

    public void delete(Long id) {
        Product p = getById(id);
        productRepository.delete(p);
    }
}
