package com.matheuss.controle_estoque_api.service;

import com.matheuss.controle_estoque_api.domain.*;
import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.domain.history.HistoryEventType;
import com.matheuss.controle_estoque_api.dto.ComputerResponseDTO;
import com.matheuss.controle_estoque_api.exception.BusinessRuleException;
import com.matheuss.controle_estoque_api.mapper.ComputerMapper;
import com.matheuss.controle_estoque_api.repository.ComponentRepository;
import com.matheuss.controle_estoque_api.repository.ComputerRepository;
import com.matheuss.controle_estoque_api.service.support.EntityResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComputerServiceTest {

    @Mock private ComputerRepository computerRepository;
    @Mock private ComponentRepository componentRepository;
    @Mock private ComputerMapper computerMapper;
    @Mock private AssetHistoryService assetHistoryService;
    @Mock private EntityResolver resolver;
    @InjectMocks private ComputerService computerService;

    private Computer computer;
    private Component componentToUninstall;
    private Component componentToInstall;
    private Category componentCategory;

    @BeforeEach
    void setUp() {
        // Configuração do Computador
        computer = new Computer();
        computer.setId(1L);
        computer.setName("DELL-G15");

        // Configuração da Categoria dos Componentes
        componentCategory = new Category("Memória RAM");
        componentCategory.setId(2L);
        componentCategory.setName("Memória RAM");

        // Configuração do Componente a ser Desinstalado
        componentToUninstall = new Component();
        componentToUninstall.setId(10L);
        componentToUninstall.setName("Kingston Fury 8GB");
        componentToUninstall.setCategory(componentCategory);
        componentToUninstall.setType("DDR4");
        componentToUninstall.setStatus(AssetStatus.EM_USO);
        componentToUninstall.setComputer(computer);

        // Configuração do Componente a ser Instalado
        componentToInstall = new Component();
        componentToInstall.setId(11L);
        componentToInstall.setName("Corsair Vengeance 8GB");
        componentToInstall.setCategory(componentCategory);
        componentToInstall.setType("DDR4");
        componentToInstall.setStatus(AssetStatus.EM_ESTOQUE);

        // Configuração da lista de componentes do computador (MUTÁVEL)
        List<Component> components = new ArrayList<>();
        components.add(componentToUninstall);
        computer.setComponents(components);
    }

    @Test
    void deveTrocarComponente_ComSucesso() {
        // ARRANGE
        when(resolver.requireComputer(1L)).thenReturn(computer);
        when(resolver.requireComponent(10L)).thenReturn(componentToUninstall);
        when(resolver.requireComponent(11L)).thenReturn(componentToInstall);
        when(computerRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(computer));
        when(computerMapper.toResponseDTO(any(Computer.class))).thenReturn(new ComputerResponseDTO());

        // ACT
        computerService.swapComponent(1L, 10L, 11L);

        // ASSERT
        // Verifica se os métodos de salvar foram chamados para todas as entidades
        verify(computerRepository).save(computer);
        verify(componentRepository).save(componentToUninstall);
        verify(componentRepository).save(componentToInstall);

        // Verifica o estado final dos objetos
        assertNull(componentToUninstall.getComputer());
        assertEquals(AssetStatus.EM_ESTOQUE, componentToUninstall.getStatus());
        assertEquals(computer, componentToInstall.getComputer());
        assertEquals(AssetStatus.EM_USO, componentToInstall.getStatus());
    }

    @Test
    void deveLancarExcecao_AoTrocarComponenteNaoInstalado() {
        // ARRANGE
        computer.getComponents().clear(); // Garante que o computador não tem o componente
        when(resolver.requireComputer(1L)).thenReturn(computer);
        when(resolver.requireComponent(10L)).thenReturn(componentToUninstall);
        when(resolver.requireComponent(11L)).thenReturn(componentToInstall);

        // ACT & ASSERT
        assertThrows(BusinessRuleException.class, () -> computerService.swapComponent(1L, 10L, 11L));
        verify(computerRepository, never()).save(any()); // Garante que nenhuma persistência ocorreu
    }

    @Test
    void deveLancarExcecao_AoInstalarComponenteQueNaoEstaEmEstoque() {
        // ARRANGE
        componentToInstall.setStatus(AssetStatus.EM_MANUTENCAO); // Componente novo não está em estoque
        when(resolver.requireComputer(1L)).thenReturn(computer);
        when(resolver.requireComponent(10L)).thenReturn(componentToUninstall);
        when(resolver.requireComponent(11L)).thenReturn(componentToInstall);

        // ACT & ASSERT
        assertThrows(BusinessRuleException.class, () -> computerService.swapComponent(1L, 10L, 11L));
        verify(computerRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecao_AoTrocarComponentesDeTiposDiferentes() {
        // ARRANGE
        componentToInstall.setType("DDR5"); // Tipos diferentes
        when(resolver.requireComputer(1L)).thenReturn(computer);
        when(resolver.requireComponent(10L)).thenReturn(componentToUninstall);
        when(resolver.requireComponent(11L)).thenReturn(componentToInstall);

        // ACT & ASSERT
        assertThrows(BusinessRuleException.class, () -> computerService.swapComponent(1L, 10L, 11L));
        verify(computerRepository, never()).save(any());
    }
}
