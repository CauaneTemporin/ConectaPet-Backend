package br.com.conectapet.service;

import br.com.conectapet.dto.AdminDTOs;
import br.com.conectapet.model.*;
import br.com.conectapet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository      userRepository;
    private final AnimalRepository    animalRepository;
    private final AdoptionRepository  adoptionRepository;
    private final DonationRepository  donationRepository;
    private final VolunteerRepository volunteerRepository;
    private final GodparentRepository godparentRepository;
    private final ContactRepository   contactRepository;

    public AdminDTOs.AdminStatsResponse stats() {
        BigDecimal donations = donationRepository.sumConfirmed();
        return new AdminDTOs.AdminStatsResponse(
            animalRepository.count(),
            animalRepository.countByStatus(Animal.AnimalStatus.DISPONIVEL),
            animalRepository.countByStatus(Animal.AnimalStatus.ADOTADO),
            userRepository.countByRole(User.Role.USER),
            adoptionRepository.count(),
            adoptionRepository.countByStatus(Adoption.AdoptionStatus.PENDENTE),
            donations != null ? donations : BigDecimal.ZERO,
            volunteerRepository.countByStatus(Volunteer.VolunteerStatus.APROVADO),
            godparentRepository.countByStatus(Godparent.GodparentStatus.APROVADO),
            contactRepository.countByRead(false)
        );
    }
}
