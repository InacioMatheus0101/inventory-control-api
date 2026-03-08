package com.matheuss.controle_estoque_api.service;

import com.matheuss.controle_estoque_api.domain.Category;
import com.matheuss.controle_estoque_api.dto.CategoryCreateDTO;
import com.matheuss.controle_estoque_api.dto.CategoryResponseDTO;
import com.matheuss.controle_estoque_api.dto.CategoryUpdateDTO;
import com.matheuss.controle_estoque_api.exception.BusinessRuleException;
import com.matheuss.controle_estoque_api.exception.ResourceAlreadyExistsException; // Import para a nova exceção
import com.matheuss.controle_estoque_api.mapper.CategoryMapper;
import com.matheuss.controle_estoque_api.repository.CategoryRepository;
import com.matheuss.controle_estoque_api.repository.ComponentRepository;
import com.matheuss.controle_estoque_api.repository.ComputerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ComputerRepository computerRepository;
    private final ComponentRepository componentRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponseDTO createCategory(CategoryCreateDTO dto) {
        // [MELHORIA] Impede a criação de categorias com nomes duplicados.
        categoryRepository.findByName(dto.getName()).ifPresent(category -> {
            throw new ResourceAlreadyExistsException("Uma categoria com o nome '" + dto.getName() + "' já existe.");
        });

        Category newCategory = categoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(newCategory);
        return categoryMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> findAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com o ID: " + id));
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryUpdateDTO dto) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com o ID: " + id));

        // [MELHORIA] Verifica se o novo nome já está em uso por outra categoria.
        categoryRepository.findByName(dto.getName()).ifPresent(category -> {
            if (!category.getId().equals(id)) {
                throw new ResourceAlreadyExistsException("Uma categoria com o nome '" + dto.getName() + "' já existe.");
            }
        });

        existing.setName(dto.getName());
        Category saved = categoryRepository.save(existing);
        return categoryMapper.toResponseDTO(saved);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com o ID: " + id));

        // A lógica de verificação está correta e foi mantida.
        boolean isInUse = computerRepository.existsByCategoryId(id) || componentRepository.existsByCategoryId(id);
        if (isInUse) {
            throw new BusinessRuleException("Não é possível deletar a categoria '" + category.getName() + "' pois ela está associada a um ou mais ativos.");
        }

        categoryRepository.delete(category); // Use delete(entity) para melhor performance com o cache do Hibernate
    }
}
