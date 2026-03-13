package com.techgear.store.controller;

import com.techgear.store.entity.Category;
import com.techgear.store.repository.CategoryRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {



        private final CategoryRepository categoryRepository;

        public CategoryController(CategoryRepository categoryRepository) {
            this.categoryRepository = categoryRepository;
        }

        @GetMapping
        public List<Category> getAll() {
            return categoryRepository.findAll();
        }

}
