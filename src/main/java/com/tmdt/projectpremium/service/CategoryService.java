package com.tmdt.projectpremium.service;

import com.tmdt.projectpremium.dto.CategoryResponseDTO;
import com.tmdt.projectpremium.entity.Category;
import com.tmdt.projectpremium.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponseDTO> getActiveCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrue();

        return categories.stream().map(category -> {
            CategoryResponseDTO dto = new CategoryResponseDTO();
            dto.setId(category.getId());
            dto.setName(category.getName());
            dto.setIcon(category.getIcon());
            return dto;
        }).collect(Collectors.toList());
    }
}
