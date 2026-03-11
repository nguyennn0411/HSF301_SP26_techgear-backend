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
        Specification<Product> spec = null;

        if (f != null) {
            if (f.getCategoryId() != null) {
                Specification<Product> s =
                        (root, q, cb) -> cb.equal(root.get("category").get("id"), f.getCategoryId());
                spec = (spec == null) ? s : spec.and(s);
            }

            if (f.getBrandId() != null) {
                Specification<Product> s =
                        (root, q, cb) -> cb.equal(root.get("brand").get("id"), f.getBrandId());
                spec = (spec == null) ? s : spec.and(s);
            }

            if (f.getMinPrice() != null) {
                Specification<Product> s =
                        (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("price"), f.getMinPrice());
                spec = (spec == null) ? s : spec.and(s);
            }

            if (f.getMaxPrice() != null) {
                Specification<Product> s =
                        (root, q, cb) -> cb.lessThanOrEqualTo(root.get("price"), f.getMaxPrice());
                spec = (spec == null) ? s : spec.and(s);
            }

            if (f.getKeyword() != null && !f.getKeyword().trim().isEmpty()) {
                String kw = "%" + f.getKeyword().trim().toLowerCase() + "%";
                Specification<Product> s =
                        (root, q, cb) -> cb.like(cb.lower(root.get("name")), kw);
                spec = (spec == null) ? s : spec.and(s);
            }

            if (Boolean.TRUE.equals(f.getActiveOnly())) {
                Specification<Product> s =
                        (root, q, cb) -> cb.isTrue(root.get("isActive"));
                spec = (spec == null) ? s : spec.and(s);
            }
        }

        return (spec == null) ? productRepository.findAll() : productRepository.findAll(spec);
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public Product create(Product p) {
        if (p.getPrice() == null || p.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be > 0");
        }

        if (p.getStock() == null || p.getStock() < 0) {
            throw new IllegalArgumentException("Stock must be >= 0");
        }

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