package br.com.conectapet.config;

import br.com.conectapet.model.*;
import br.com.conectapet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository     userRepository;
    private final AnimalRepository   animalRepository;
    private final ShelterRepository  shelterRepository;
    private final PasswordEncoder    passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        log.info("✅ Dados iniciais carregados.");
    }

    // ── USERS ────────────────────────────────────────────────
    private void seedUsers() {
        if (userRepository.existsByEmail("admin@conectapet.org")) return;

        userRepository.save(User.builder()
            .name("Admin Conecta PET")
            .email("admin@conectapet.org")
            .password(passwordEncoder.encode("admin123"))
            .city("São Paulo")
            .role(User.Role.ADMIN)
            .bio("Administrador do sistema Conecta PET.")
            .build());

        userRepository.save(User.builder()
            .name("Cauâne Temporin")
            .email("demo@conectapet.org")
            .password(passwordEncoder.encode("demo123"))
            .city("São Paulo")
            .role(User.Role.USER)
            .bio("Amante dos animais e voluntária da causa.")
            .build());

        log.info("  👤 Usuários padrão criados.");
    }

    // ── SHELTERS ─────────────────────────────────────────────
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

    // ── ANIMALS ──────────────────────────────────────────────
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

            Animal.builder().name("Mel").species(Animal.Species.CACHORRO)
                .breed("SRD").gender(Animal.Gender.FEMEA).ageYears(3.0)
                .size(Animal.Size.GRANDE).vaccinated(true).neutered(false)
                .description("Ideal para famílias ativas. Muito carinhosa.")
                .temperament("Dócil, calma, amorosa")
                .photoUrl("🐶").shelter(shelter).build(),

            Animal.builder().name("Thor").species(Animal.Species.CACHORRO)
                .breed("Husky Siberiano").gender(Animal.Gender.MACHO).ageYears(4.0)
                .size(Animal.Size.GRANDE).vaccinated(true).neutered(false)
                .description("Ideal para famílias ativas. Precisa de espaço.")
                .temperament("Ativo, brincalhão, independente")
                .photoUrl("🐩").shelter(shelter).build(),

            Animal.builder().name("Duke").species(Animal.Species.CACHORRO)
                .breed("Labrador").gender(Animal.Gender.MACHO).ageYears(3.0)
                .size(Animal.Size.GRANDE).vaccinated(true).neutered(true)
                .description("Ideal para famílias ativas. Muito obediente.")
                .temperament("Calmo, obediente, leal")
                .photoUrl("🐕‍🦺").shelter(shelter).build(),

            Animal.builder().name("Max").species(Animal.Species.CACHORRO)
                .breed("Poodle").gender(Animal.Gender.MACHO).ageYears(5.0)
                .size(Animal.Size.MEDIO).vaccinated(true).neutered(true)
                .description("Ideal para famílias ativas. Muito inteligente.")
                .temperament("Inteligente, energético, sociável")
                .photoUrl("🐶").shelter(shelter).build(),

            Animal.builder().name("Meg").species(Animal.Species.CACHORRO)
                .breed("Shih Tzu").gender(Animal.Gender.FEMEA).ageYears(4.0)
                .size(Animal.Size.PEQUENO).vaccinated(true).neutered(false)
                .description("Ideal para famílias ativas. Adora colo.")
                .temperament("Calma, carinhosa, dócil")
                .photoUrl("🐕").shelter(shelter).build(),

            Animal.builder().name("Luna").species(Animal.Species.GATO)
                .breed("SRD").gender(Animal.Gender.FEMEA).ageYears(1.0)
                .size(Animal.Size.PEQUENO).vaccinated(true).neutered(true)
                .description("Ideal para famílias ativas. Gosta de colo.")
                .temperament("Dócil, carinhosa, tranquila")
                .photoUrl("🐱").shelter(shelter).build(),

            Animal.builder().name("Ozzi").species(Animal.Species.GATO)
                .breed("SRD").gender(Animal.Gender.MACHO).ageYears(2.0)
                .size(Animal.Size.PEQUENO).vaccinated(true).neutered(false)
                .description("Ideal para famílias ativas. Curioso e ativo.")
                .temperament("Brincalhão, curioso, sociável")
                .photoUrl("🐈").shelter(shelter).build(),

            Animal.builder().name("Mali").species(Animal.Species.GATO)
                .breed("SRD").gender(Animal.Gender.FEMEA).ageYears(3.0)
                .size(Animal.Size.PEQUENO).vaccinated(true).neutered(true)
                .description("Ideal para famílias ativas. Muito tranquila.")
                .temperament("Dócil, calma, independente")
                .photoUrl("🐈‍⬛").shelter(shelter).build(),

            Animal.builder().name("Pipoca").species(Animal.Species.OUTRO)
                .breed("Coelho Anão").gender(Animal.Gender.FEMEA).ageYears(2.0)
                .size(Animal.Size.PEQUENO).vaccinated(true).neutered(false)
                .description("Dócil e muito carinhosa. Ótima para crianças.")
                .temperament("Tranquila, curiosa, dócil")
                .photoUrl("🐇").shelter(shelter).build(),
        };

        for (var a : animals) animalRepository.save(a);
        log.info("  🐾 {} animais cadastrados.", animals.length);
    }
}
