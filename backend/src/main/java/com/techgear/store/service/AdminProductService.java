package com.techgear.store.service;

import com.techgear.store.dto.ProductRequest;
import com.techgear.store.dto.ProductResponse;
import com.techgear.store.entity.Brand;
import com.techgear.store.entity.Category;
import com.techgear.store.entity.Product;
import com.techgear.store.repository.BrandRepository;
import com.techgear.store.repository.CategoryRepository;
import com.techgear.store.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public AdminProductService(ProductRepository productRepository,
                               CategoryRepository categoryRepository,
                               BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
    }

    public List<ProductResponse> getAll(String keyword, Long categoryId, Long brandId, Boolean activeOnly) {
        Specification<Product> spec = null;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = "%" + keyword.trim().toLowerCase() + "%";
            Specification<Product> s = (root, q, cb) -> cb.like(cb.lower(root.get("name")), kw);
            spec = (spec == null) ? s : spec.and(s);
        }

        if (categoryId != null) {
            Specification<Product> s = (root, q, cb) -> cb.equal(root.get("category").get("id"), categoryId);
            spec = (spec == null) ? s : spec.and(s);
        }

        if (brandId != null) {
            Specification<Product> s = (root, q, cb) -> cb.equal(root.get("brand").get("id"), brandId);
            spec = (spec == null) ? s : spec.and(s);
        }

        if (Boolean.TRUE.equals(activeOnly)) {
            Specification<Product> s = (root, q, cb) -> cb.isTrue(root.get("isActive"));
            spec = (spec == null) ? s : spec.and(s);
        }

        List<Product> products = (spec == null)
                ? productRepository.findAll()
                : productRepository.findAll(spec);

        List<ProductResponse> responses = new ArrayList<>();
        for (Product p : products) {
            responses.add(mapResponse(p));
        }

        return responses;
    }

    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapResponse(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        validateRequest(request);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setColor(request.getColor());
        product.setSize(request.getSize());
        product.setImageUrl(request.getImageUrl());
        product.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        product.setCategory(category);
        product.setBrand(brand);

        return mapResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        validateRequest(request);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setColor(request.getColor());
        product.setSize(request.getSize());
        product.setImageUrl(request.getImageUrl());
        product.setIsActive(request.getIsActive());
        product.setCategory(category);
        product.setBrand(brand);

        return mapResponse(productRepository.save(product));
    }

    @Transactional
    public String delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productRepository.delete(product);
        return "Delete product successfully";
    }

    private void validateRequest(ProductRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be > 0");
        }
        if (request.getStock() == null || request.getStock() < 0) {
            throw new IllegalArgumentException("Stock must be >= 0");
        }
        if (request.getCategoryId() == null) {
            throw new IllegalArgumentException("Category is required");
        }
        if (request.getBrandId() == null) {
            throw new IllegalArgumentException("Brand is required");
        }
    }

    private ProductResponse mapResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStock(),
                p.getColor(),
                p.getSize(),
                p.getImageUrl(),
                p.getIsActive(),
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getCategory() != null ? p.getCategory().getName() : null,
                p.getBrand() != null ? p.getBrand().getId() : null,
                p.getBrand() != null ? p.getBrand().getName() : null
        );
    }
}
