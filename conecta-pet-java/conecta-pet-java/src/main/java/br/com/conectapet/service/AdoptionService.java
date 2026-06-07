package br.com.conectapet.service;

import br.com.conectapet.config.TenantContext;
import br.com.conectapet.dto.AdoptionDTOs;
import br.com.conectapet.model.*;
import br.com.conectapet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AdoptionService {

    private final AdoptionRepository  adoptionRepository;
    private final AnimalRepository    animalRepository;
    private final UserRepository      userRepository;
    private final GodparentRepository godparentRepository;

    /** Solicita adoção de um animal */
    @Transactional
    public AdoptionDTOs.AdoptionResponse request(
            AdoptionDTOs.AdoptionRequest req, String userEmail) {

        var user   = findUser(userEmail);
        var animal = findAnimal(req.animalId());

        if (animal.getStatus() != Animal.AnimalStatus.DISPONIVEL) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Animal não está disponível para adoção.");
        }

        boolean alreadyRequested = adoptionRepository
            .existsByUserAndAnimalAndStatusNot(user, animal, Adoption.AdoptionStatus.RECUSADO);
        if (alreadyRequested) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Você já possui uma solicitação ativa para este animal.");
        }

        var adoption = Adoption.builder()
            .user(user)
            .animal(animal)
            .message(req.message())
            .status(Adoption.AdoptionStatus.PENDENTE)
            .build();

        return AdoptionDTOs.AdoptionResponse.from(adoptionRepository.save(adoption));
    }

    /** Lista adoções do usuário autenticado */
    public List<AdoptionDTOs.AdoptionResponse> myAdoptions(String userEmail) {
        var user = findUser(userEmail);
        return adoptionRepository.findByUserOrderByCreatedAtDesc(user)
            .stream().map(AdoptionDTOs.AdoptionResponse::from).toList();
    }

    /** Lista todas as adoções (admin) */
    public List<AdoptionDTOs.AdoptionResponse> listAll(String status) {
        Long ongId = TenantContext.get();
        List<Adoption> list;

        if (ongId != null) {
            list = status != null
                ? adoptionRepository.findByAnimalOngIdAndStatus(ongId, parseStatus(status))
                : adoptionRepository.findByAnimalOngIdOrderByCreatedAtDesc(ongId);
        } else {
            list = status != null
                ? adoptionRepository.findByStatus(parseStatus(status))
                : adoptionRepository.findAllByOrderByCreatedAtDesc();
        }

        return list.stream().map(AdoptionDTOs.AdoptionResponse::from).toList();
    }

    /** Revisa solicitação: aprovado | recusado | concluido (admin) */
    @Transactional
    public AdoptionDTOs.AdoptionResponse review(
            Long adoptionId, AdoptionDTOs.ReviewRequest req, String adminEmail) {

        var adoption = adoptionRepository.findById(adoptionId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Solicitação não encontrada."));

        var admin = findUser(adminEmail);
        adoption.setStatus(req.status());
        adoption.setReviewedBy(admin);
        adoption.setReviewedAt(LocalDateTime.now());

        // Ao concluir adoção, marca o animal como adotado e desativa apadrinhamentos ativos
        if (req.status() == Adoption.AdoptionStatus.CONCLUIDO) {
            var animal = adoption.getAnimal();
            animal.setStatus(Animal.AnimalStatus.ADOTADO);
            animalRepository.save(animal);

            var activeGodparents = godparentRepository.findByAnimalAndStatusIn(animal,
                Arrays.asList(Godparent.GodparentStatus.APROVADO, Godparent.GodparentStatus.PENDENTE));
            activeGodparents.forEach(gp -> gp.setStatus(Godparent.GodparentStatus.INATIVO));
            godparentRepository.saveAll(activeGodparents);
        }

        return AdoptionDTOs.AdoptionResponse.from(adoptionRepository.save(adoption));
    }

    // ── Helpers ─────────────────────────────────────────────
    private User findUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Usuário não encontrado."));
    }

    private Animal findAnimal(Long id) {
        return animalRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Animal não encontrado."));
    }

    private Adoption.AdoptionStatus parseStatus(String s) {
        try { return Adoption.AdoptionStatus.valueOf(s.toUpperCase()); }
        catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido: " + s);
        }
    }
}
