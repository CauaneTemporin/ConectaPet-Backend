package br.com.conectapet.service;

import br.com.conectapet.dto.GodparentDTOs;
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
public class GodparentService {

    private final GodparentRepository godparentRepository;
    private final AnimalRepository    animalRepository;
    private final UserRepository      userRepository;
    private final DonationRepository  donationRepository;

    @Transactional
    public GodparentDTOs.GodparentResponse register(GodparentDTOs.GodparentRequest req, String userEmail) {
        var user   = findUser(userEmail);
        var animal = animalRepository.findById(req.animalId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal não encontrado."));
        if (animal.getStatus() == Animal.AnimalStatus.ADOTADO)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Animal já foi adotado.");
        if (godparentRepository.existsByUserAndAnimalAndStatus(user, animal, Godparent.GodparentStatus.APROVADO))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já apadrinhou este animal.");
        var g = Godparent.builder().user(user).animal(animal).amount(req.amount()).build();
        return GodparentDTOs.GodparentResponse.from(godparentRepository.save(g));
    }

    public List<GodparentDTOs.GodparentResponse> myGodparents(String userEmail) {
        return godparentRepository.findByUserOrderByStartedAtDesc(findUser(userEmail))
            .stream().map(GodparentDTOs.GodparentResponse::from).toList();
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
    }

    @Transactional
    public GodparentDTOs.GodparentResponse deactivateGodparent(Long id, String email) {
        Godparent godparent = godparentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Apadrinhamiento não encontrado"));

        User user = findUser(email);
        if (!godparent.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para desativar este apadrinhamimiento");
        }

        godparent.setStatus(Godparent.GodparentStatus.INATIVO);
        Godparent updated = godparentRepository.save(godparent);

        // Cancelar doações associadas ao apadrinhamimiento
        cancelGodparentDonations(user, godparent.getAnimal());

        return GodparentDTOs.GodparentResponse.from(updated);
    }

    @Transactional
    public void deleteGodparent(Long id, String email) {
        Godparent godparent = godparentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Apadrinhamiento não encontrado"));

        User user = findUser(email);
        if (!godparent.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para deletar este apadrinhamimiento");
        }

        // Cancelar doações associadas ao apadrinhamimiento
        cancelGodparentDonations(user, godparent.getAnimal());

        godparentRepository.deleteById(id);
    }

    /**
     * Cancela todas as doações ativas do usuário relacionadas ao animal
     */
    private void cancelGodparentDonations(User user, Animal animal) {
        List<Donation> donations = donationRepository.findByUserOrderByCreatedAtDesc(user);

        donations.stream()
            .filter(d -> d.getStatus() != Donation.DonationStatus.CANCELADO)
            .forEach(d -> {
                d.setStatus(Donation.DonationStatus.CANCELADO);
                donationRepository.save(d);
            });
    }
}
