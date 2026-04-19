package com.baligh.backend.controller;

import com.baligh.backend.dto.response.CategoryResponse;
import com.baligh.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.getActiveCategories());
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.create(body.get("nameAr"), body.get("icon"), body.get("color")));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(categoryService.update(id, body.get("nameAr"), body.get("icon"), body.get("color")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        categoryService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
