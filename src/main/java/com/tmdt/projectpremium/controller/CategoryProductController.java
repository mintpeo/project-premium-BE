package com.tmdt.projectpremium.controller;

import com.tmdt.projectpremium.dto.response.CategoryProductRes;
import com.tmdt.projectpremium.service.CategoryProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/category")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RequiredArgsConstructor
public class CategoryProductController {
    private final CategoryProductService service;

    @GetMapping("/{category}")
    public List<CategoryProductRes> getByCategory(@PathVariable String category) {
        return service.getByCategory(category);
    }
}
