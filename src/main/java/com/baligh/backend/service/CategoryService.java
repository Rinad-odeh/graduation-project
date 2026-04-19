package com.baligh.backend.service;

import com.baligh.backend.dto.response.CategoryResponse;
import com.baligh.backend.exception.ResourceNotFoundException;
import com.baligh.backend.model.Category;
import com.baligh.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAr()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional
    public CategoryResponse create(String nameAr, String icon, String color) {
        Category category = Category.builder()
                .nameAr(nameAr)
                .icon(icon)
                .color(color)
                .build();
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, String nameAr, String icon, String color) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Category", id));
        if (nameAr != null) category.setNameAr(nameAr);
        if (icon != null) category.setIcon(icon);
        if (color != null) category.setColor(color);
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public void deactivate(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Category", id));
        category.setActive(false);
        categoryRepository.save(category);
    }
}
