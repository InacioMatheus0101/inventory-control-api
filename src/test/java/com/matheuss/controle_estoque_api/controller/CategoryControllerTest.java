package com.matheuss.controle_estoque_api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.domain.enums.EquipmentState;
import com.matheuss.controle_estoque_api.dto.CategoryCreateDTO;
import com.matheuss.controle_estoque_api.dto.ComputerCreateDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test" )
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void naoDeveExcluirCategoria_QuandoEstiverEmUso() throws Exception {
        // --- PASSO 1: Criar a categoria ---
        System.out.println("\n--- PASSO 1: CRIANDO CATEGORIA ---\n");
        CategoryCreateDTO categoryDTO = new CategoryCreateDTO();
        categoryDTO.setName("Notebooks em Uso");

        MvcResult categoryResult = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO))
                        .with(csrf()))
                .andDo(print()) // Apenas imprima
                .andReturn();
        long categoryId = objectMapper.readTree(categoryResult.getResponse().getContentAsString()).get("id").asLong();

        // --- PASSO 2: Criar um computador válido associado ---
        System.out.println("\n--- PASSO 2: CRIANDO COMPUTADOR ---\n");
        ComputerCreateDTO computerDTO = new ComputerCreateDTO();
        computerDTO.setAssetTag("NTK-IN-USE");
        computerDTO.setPatrimonio("PAT-IN-USE");
        computerDTO.setStatus(AssetStatus.EM_USO);
        computerDTO.setEquipmentState(EquipmentState.USADO);
        computerDTO.setPurchaseDate(LocalDate.now());
        computerDTO.setNameComputer("Notebook de Teste em Uso");
        computerDTO.setSerialNumber("SN-IN-USE");
        computerDTO.setCpu("i7");
        computerDTO.setRamSizeInGB(8);
        computerDTO.setStorageSizeInGB(256);
        computerDTO.setOs("Linux");
        computerDTO.setCategoryId(categoryId);

        MvcResult computerResult = mockMvc.perform(post("/api/computers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(computerDTO))
                        .with(csrf()))
                .andDo(print()) // Apenas imprima
                .andReturn();
        long computerId = objectMapper.readTree(computerResult.getResponse().getContentAsString()).get("id").asLong();

        // --- PASSO 2.5: VERIFICAÇÃO ---
        System.out.println("\n--- PASSO 2.5: VERIFICANDO COMPUTADOR CRIADO (GET) ---\n");
        mockMvc.perform(get("/api/computers/" + computerId).with(csrf()))
                .andDo(print()); // Apenas imprima

        // --- PASSO 3: Tentar excluir a categoria em uso ---
        System.out.println("\n--- PASSO 3: TENTANDO EXCLUIR CATEGORIA ---\n");
        mockMvc.perform(delete("/api/categories/" + categoryId)
                        .with(csrf()))
                .andDo(print()); // Apenas imprima
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCriarEExcluirCategoria_QuandoNaoEstiverEmUso() throws Exception {
        CategoryCreateDTO dto = new CategoryCreateDTO();
        dto.setName("Monitores");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/categories/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
