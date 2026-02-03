package com.techgear.store.controller;

import com.techgear.store.dto.ProductFilterDTO;
import com.techgear.store.entity.Product;
import com.techgear.store.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) { this.productService = productService; }

    @PostMapping("/filter")
    public List<Product> filter(@RequestBody(required = false) ProductFilterDTO filter) {
        return productService.getAll(filter);
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PostMapping
    public Product create(@RequestBody Product p) {
        return productService.create(p);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product p) {
        return productService.update(id, p);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
