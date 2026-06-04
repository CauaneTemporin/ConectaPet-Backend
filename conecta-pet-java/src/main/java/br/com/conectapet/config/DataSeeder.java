package br.com.conectapet.config;

import br.com.conectapet.model.*;
import br.com.conectapet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final AnimalRepository  animalRepository;
    private final ShelterRepository shelterRepository;
    private final JdbcTemplate      jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) {
        executarDataSql();
        seedShelters();
        seedAnimals();
        log.info("✅ Dados iniciais carregados.");
    }

    private void executarDataSql() {
        try {
            var resource = new ClassPathResource("data.sql");
            if (!resource.exists()) return;

            String sql;
            try (var reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                sql = reader.lines().collect(Collectors.joining("\n"));
            }

            // Remove comentários e executa cada statement separadamente
            for (String statement : sql.split(";")) {
                String trimmed = statement.trim()
                    .replaceAll("--[^\n]*", "")   // remove comentários
                    .trim();
                if (!trimmed.isEmpty()) {
                    try {
                        jdbcTemplate.execute(trimmed);
                    } catch (Exception e) {
                        log.debug("SQL ignorado (provavelmente já existe): {}", e.getMessage());
                    }
                }
            }

            log.info("  📄 data.sql executado.");
        } catch (Exception e) {
            log.warn("  ⚠️ Erro ao executar data.sql: {}", e.getMessage());
        }
    }

    private void seedShelters() {
        if (shelterRepository.count() > 0) return;

        shelterRepository.save(Shelter.builder()
            .name("Abrigo Patinhas Felizes")
            .city("São Paulo")
            .address("Rua das Flores, 100 — Vila Mariana")
            .phone("(11) 3000-1111")
            .email("patinhas@sp.org.br")
            .build());

        shelterRepository.save(Shelter.builder()
            .name("Lar dos Bichinhos")
            .city("Campinas")
            .address("Av. Brasil, 500 — Centro")
            .phone("(19) 3200-2222")
            .email("lar@bichinhos.org.br")
            .build());

        log.info("  🏠 Abrigos cadastrados.");
    }

    private void seedAnimals() {
        if (animalRepository.count() > 0) return;

        var shelter = shelterRepository.findAll().get(0);

        var animals = new Animal[]{
            Animal.builder().name("Belinha").species(Animal.Species.CACHORRO)
                .breed("Labrador").gender(Animal.Gender.FEMEA).ageYears(2.0)
                .size(Animal.Size.GRANDE).vaccinated(true).neutered(true)
                .description("Ideal para famílias ativas. Adora brincar ao ar livre.")
                .temperament("Energética, dócil, brincalhona")
                .photoUrl("🐕").shelter(shelter).build(),

            Animal.builder().name("Luna").species(Animal.Species.GATO)
                .breed("SRD").gender(Animal.Gender.FEMEA).ageYears(1.0)
                .size(Animal.Size.PEQUENO).vaccinated(true).neutered(true)
                .description("Gosta de colo e é muito tranquila.")
                .temperament("Dócil, carinhosa, tranquila")
                .photoUrl("🐱").shelter(shelter).build(),
        };

        for (var a : animals) animalRepository.save(a);
        log.info("  🐾 {} animais cadastrados.", animals.length);
    }
}
