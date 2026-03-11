package com.techgear.store.controller;

import com.techgear.store.dto.ProductFilterDTO;
import com.techgear.store.entity.Product;
import com.techgear.store.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Home page: lấy danh sách sản phẩm
    @GetMapping
    public List<Product> getAllProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean activeOnly
    ) {
        ProductFilterDTO filter = new ProductFilterDTO();
        filter.setCategoryId(categoryId);
        filter.setBrandId(brandId);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        filter.setKeyword(keyword);
        filter.setActiveOnly(activeOnly);

        return productService.getAll(filter);
    }

    // Product detail page: lấy chi tiết theo id
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.create(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.update(id, product);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return "Delete product successfully";
    }
}