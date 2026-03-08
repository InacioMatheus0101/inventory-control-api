package com.matheuss.controle_estoque_api.service;

import com.matheuss.controle_estoque_api.domain.Category;
import com.matheuss.controle_estoque_api.dto.CategoryCreateDTO;
import com.matheuss.controle_estoque_api.dto.CategoryResponseDTO;
import com.matheuss.controle_estoque_api.exception.BusinessRuleException;
import com.matheuss.controle_estoque_api.exception.ResourceAlreadyExistsException;
import com.matheuss.controle_estoque_api.mapper.CategoryMapper;
import com.matheuss.controle_estoque_api.repository.CategoryRepository;
import com.matheuss.controle_estoque_api.repository.ComponentRepository;
import com.matheuss.controle_estoque_api.repository.ComputerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ComputerRepository computerRepository;
    @Mock
    private ComponentRepository componentRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryCreateDTO createDTO;
    private CategoryResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        category = new Category("Notebooks");
        category.setId(1L);

        createDTO = new CategoryCreateDTO();
        createDTO.setName("Notebooks");

         responseDTO = new CategoryResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Notebooks");
    }

    @Test
    void deveCriarCategoria_QuandoNomeNaoExistir() {
        // Configuração
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(categoryMapper.toEntity(any(CategoryCreateDTO.class))).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponseDTO(any(Category.class))).thenReturn(responseDTO);

        // Execução
        CategoryResponseDTO result = categoryService.createCategory(createDTO);

        // Verificação
        assertNotNull(result);
        assertEquals("Notebooks", result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void deveLancarExcecao_QuandoCriarCategoriaComNomeQueJaExiste() {
        // Configuração
        when(categoryRepository.findByName("Notebooks")).thenReturn(Optional.of(category));

        // Execução e Verificação
        Exception exception = assertThrows(ResourceAlreadyExistsException.class, () -> {
            categoryService.createCategory(createDTO);
        });

        assertEquals("Uma categoria com o nome 'Notebooks' já existe.", exception.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deveExcluirCategoria_QuandoNaoEstiverEmUso() {
        // Configuração
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(computerRepository.existsByCategoryId(1L)).thenReturn(false);
        when(componentRepository.existsByCategoryId(1L)).thenReturn(false);

        // Execução
        assertDoesNotThrow(() -> categoryService.deleteCategory(1L));

        // Verificação
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deveLancarExcecao_QuandoExcluirCategoriaEmUsoPorComputador() {
        // Configuração
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(computerRepository.existsByCategoryId(1L)).thenReturn(true);

        // Execução e Verificação
        Exception exception = assertThrows(BusinessRuleException.class, () -> {
            categoryService.deleteCategory(1L);
        });

        assertEquals("Não é possível deletar a categoria 'Notebooks' pois ela está associada a um ou mais ativos.", exception.getMessage());
        verify(categoryRepository, never()).delete(any());
    }
    
    @Test
    void deveLancarExcecao_QuandoExcluirCategoriaEmUsoPorComponente() {
        // Configuração
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(computerRepository.existsByCategoryId(1L)).thenReturn(false); // Não está em uso por computador
        when(componentRepository.existsByCategoryId(1L)).thenReturn(true); // Mas está em uso por componente

        // Execução e Verificação
        Exception exception = assertThrows(BusinessRuleException.class, () -> {
            categoryService.deleteCategory(1L);
        });

        assertEquals("Não é possível deletar a categoria 'Notebooks' pois ela está associada a um ou mais ativos.", exception.getMessage());
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void deveLancarExcecao_QuandoExcluirCategoriaNaoExistente() {
        // Configuração
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // Execução e Verificação
        assertThrows(EntityNotFoundException.class, () -> {
            categoryService.deleteCategory(99L);
        });
    }
}
