package br.com.conectapet.service;

import br.com.conectapet.config.TenantContext;
import br.com.conectapet.dto.AnimalDTOs;
import br.com.conectapet.model.*;
import br.com.conectapet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository  animalRepository;
    private final ShelterRepository shelterRepository;
    private final UserRepository    userRepository;
    private final OngRepository     ongRepository;

    /** Lista animais com filtros opcionais */
    public List<AnimalDTOs.AnimalResponse> list(String species, String size, String status, String q) {
        Animal.Species      speciesEnum = parseEnum(Animal.Species.class,      species);
        Animal.Size         sizeEnum    = parseEnum(Animal.Size.class,         size != null ? size.toUpperCase() : null);
        Animal.AnimalStatus statusEnum  = parseEnum(Animal.AnimalStatus.class, status != null ? status.toUpperCase() : null);

        Long ongId = TenantContext.get();
        List<Animal> animals = ongId != null
            ? animalRepository.searchByOng(ongId, speciesEnum, sizeEnum, statusEnum, q)
            : animalRepository.search(speciesEnum, sizeEnum, statusEnum, q);

        return animals.stream().map(AnimalDTOs.AnimalResponse::from).toList();
    }

    /** Busca animal por ID */
    public AnimalDTOs.AnimalResponse findById(Long id) {
        return animalRepository.findById(id)
            .map(AnimalDTOs.AnimalResponse::from)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Animal não encontrado."));
    }

    /** Cria novo animal (admin) */
    @Transactional
    public AnimalDTOs.AnimalResponse create(AnimalDTOs.AnimalRequest req, String adminEmail) {
        var animal = Animal.builder()
            .name(req.name().trim())
            .species(req.species())
            .breed(req.breed())
            .gender(req.gender())
            .ageYears(req.ageYears())
            .size(req.size() != null ? req.size() : Animal.Size.MEDIO)
            .description(req.description())
            .temperament(req.temperament())
            .vaccinated(Boolean.TRUE.equals(req.vaccinated()))
            .neutered(Boolean.TRUE.equals(req.neutered()))
            .status(req.status() != null ? req.status() : Animal.AnimalStatus.DISPONIVEL)
            .photoUrl(req.photoUrl())
            .build();

        if (req.shelterId() != null) {
            shelterRepository.findById(req.shelterId()).ifPresent(animal::setShelter);
        }

        Long ongId = TenantContext.get();
        if (ongId != null) {
            ongRepository.findById(ongId).ifPresent(animal::setOng);
        }

        userRepository.findByEmail(adminEmail).ifPresent(animal::setCreatedBy);
        return AnimalDTOs.AnimalResponse.from(animalRepository.save(animal));
    }

    /** Atualiza campos do animal (admin) */
    @Transactional
    public AnimalDTOs.AnimalResponse update(Long id, AnimalDTOs.AnimalUpdateRequest req) {
        var animal = animalRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Animal não encontrado."));

        if (req.name()        != null) animal.setName(req.name().trim());
        if (req.species()     != null) animal.setSpecies(req.species());
        if (req.breed()       != null) animal.setBreed(req.breed());
        if (req.gender()      != null) animal.setGender(req.gender());
        if (req.ageYears()    != null) animal.setAgeYears(req.ageYears());
        if (req.size()        != null) animal.setSize(req.size());
        if (req.description() != null) animal.setDescription(req.description());
        if (req.temperament() != null) animal.setTemperament(req.temperament());
        if (req.vaccinated()  != null) animal.setVaccinated(req.vaccinated());
        if (req.neutered()    != null) animal.setNeutered(req.neutered());
        if (req.status()      != null) animal.setStatus(req.status());
        if (req.photoUrl()    != null) animal.setPhotoUrl(req.photoUrl());

        return AnimalDTOs.AnimalResponse.from(animalRepository.save(animal));
    }

    /** Remove animal (admin) */
    @Transactional
    public void delete(Long id) {
        if (!animalRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal não encontrado.");
        }
        animalRepository.deleteById(id);
    }

    // ── Helper ──────────────────────────────────────────────
    private <E extends Enum<E>> E parseEnum(Class<E> clazz, String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(clazz, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
