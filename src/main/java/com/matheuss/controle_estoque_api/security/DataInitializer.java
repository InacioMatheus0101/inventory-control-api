package com.matheuss.controle_estoque_api.security;

// Seus imports corrigidos
import com.matheuss.controle_estoque_api.domain.enums.AssetStatus;
import com.matheuss.controle_estoque_api.domain.enums.EquipmentState;
import com.matheuss.controle_estoque_api.domain.Category;
import com.matheuss.controle_estoque_api.repository.CategoryRepository;
import com.matheuss.controle_estoque_api.domain.Computer;
import com.matheuss.controle_estoque_api.repository.ComputerRepository;
import com.matheuss.controle_estoque_api.domain.Location;
import com.matheuss.controle_estoque_api.repository.LocationRepository;
import com.matheuss.controle_estoque_api.security.User;
import com.matheuss.controle_estoque_api.security.UserRepository;
import com.matheuss.controle_estoque_api.security.UserRole;
import com.matheuss.controle_estoque_api.security.UserRoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private UserRoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private LocationRepository locationRepository;
    @Autowired private ComputerRepository computerRepository;

    @Override
    public void run(String... args) throws Exception {
        // --- Criação de Usuários e Perfis ---
        UserRole adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
            UserRole role = new UserRole();
            role.setName("ROLE_ADMIN");
            return roleRepository.save(role);
        });
        UserRole techRole = roleRepository.findByName("ROLE_TECHNICIAN").orElseGet(() -> {
            UserRole role = new UserRole();
            role.setName("ROLE_TECHNICIAN");
            return roleRepository.save(role);
        });

        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRoles(Set.of(adminRole, techRole));
            userRepository.save(adminUser);
        }
        
        if (userRepository.findByUsername("tech").isEmpty()) {
            User techUser = new User();
            techUser.setUsername("tech");
            techUser.setPassword(passwordEncoder.encode("tech123"));
            techUser.setRoles(Set.of(techRole));
            userRepository.save(techUser);
        }

        // --- Criação de Dados de Teste para Ativos ---
        if (computerRepository.count() == 0) {
            System.out.println(">>> Populando banco de dados com dados de teste...");

            // 1. Criar Categorias
            Category notebookCat = new Category();
            notebookCat.setName("Notebook");
            categoryRepository.save(notebookCat);

            // 2. Criar Localizações (CORRIGIDO para usar os campos da sua entidade Location)
            Location escritorioSP = new Location();
            escritorioSP.setPaNumber("SP-01");
            escritorioSP.setFloor("15º Andar");
            escritorioSP.setSector("Desenvolvimento");
            locationRepository.save(escritorioSP);

            Location estoqueRJ = new Location();
            estoqueRJ.setPaNumber("RJ-ALM");
            estoqueRJ.setFloor("Térreo");
            estoqueRJ.setSector("Almoxarifado Central");
            locationRepository.save(estoqueRJ);

            // 3. Criar Computadores (Ativos) com status variados
            Computer c1 = new Computer();
            c1.setPatrimonio("NT001");
            c1.setPurchaseDate(LocalDate.now().minusMonths(6));
            c1.setStatus(AssetStatus.EM_USO);
            c1.setEquipmentState(EquipmentState.USADO); // CORRIGIDO: BOM -> USADO
            c1.setCategory(notebookCat);
            c1.setLocation(escritorioSP);
            
            Computer c2 = new Computer();
            c2.setPatrimonio("NT002");
            
            c2.setPurchaseDate(LocalDate.now().minusMonths(2));
            c2.setStatus(AssetStatus.EM_USO);
            c2.setEquipmentState(EquipmentState.USADO); // CORRIGIDO: BOM -> USADO
            c2.setCategory(notebookCat);
            c2.setLocation(escritorioSP);

            Computer c3 = new Computer();
            c3.setPatrimonio("NT003");
            
            c3.setPurchaseDate(LocalDate.now().minusDays(10));
            c3.setStatus(AssetStatus.EM_ESTOQUE);
            c3.setEquipmentState(EquipmentState.NOVO);
            c3.setCategory(notebookCat);
            c3.setLocation(estoqueRJ);

            Computer c4 = new Computer();
            c4.setPatrimonio("NT004");
            
            c4.setPurchaseDate(LocalDate.now().minusYears(1));
            c4.setStatus(AssetStatus.EM_MANUTENCAO);
            c4.setEquipmentState(EquipmentState.REPARADO); // CORRIGIDO: REGULAR -> REPARADO
            c4.setCategory(notebookCat);
            c4.setLocation(estoqueRJ);

            Computer c5 = new Computer();
            c5.setPatrimonio("NT005");
            
            c5.setPurchaseDate(LocalDate.now().minusDays(5));
            c5.setStatus(AssetStatus.EM_ESTOQUE);
            c5.setEquipmentState(EquipmentState.NOVO);
            c5.setCategory(notebookCat);
            c5.setLocation(estoqueRJ);

            computerRepository.saveAll(List.of(c1, c2, c3, c4, c5));
            
            System.out.println(">>> Dados de teste populados com sucesso!");
            System.out.println(">>> Resumo: 2 EM_USO, 2 EM_ESTOQUE, 1 EM_MANUTENCAO");
        }
    }
}
