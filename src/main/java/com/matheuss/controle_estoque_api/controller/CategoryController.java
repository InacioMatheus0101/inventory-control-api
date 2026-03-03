package com.matheuss.controle_estoque_api.controller;

import com.matheuss.controle_estoque_api.dto.CategoryCreateDTO;
import com.matheuss.controle_estoque_api.dto.CategoryResponseDTO;
import com.matheuss.controle_estoque_api.dto.CategoryUpdateDTO;
import com.matheuss.controle_estoque_api.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import necessário
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categories" )
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody @Valid CategoryCreateDTO dto) {
        CategoryResponseDTO createdCategory = categoryService.createCategory(dto);
        URI locationUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdCategory.getId()).toUri();
        return ResponseEntity.created(locationUri).body(createdCategory);
    }

    @GetMapping
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(categoryService.findCategoryById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable("id") Long id, @RequestBody @Valid CategoryUpdateDTO dto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
