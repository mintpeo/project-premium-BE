package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.dto.CategoryResponseDTO;
import com.tmdt.projectpremium.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllActiveCategories() {
        List<CategoryResponseDTO> categories = categoryService.getActiveCategories();
        return ResponseEntity.ok(categories);
    }
}
