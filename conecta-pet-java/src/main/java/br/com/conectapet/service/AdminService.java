package br.com.conectapet.service;

import br.com.conectapet.config.TenantContext;
import br.com.conectapet.dto.AdminDTOs;
import br.com.conectapet.model.*;
import br.com.conectapet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository       userRepository;
    private final AnimalRepository     animalRepository;
    private final AdoptionRepository   adoptionRepository;
    private final DonationRepository   donationRepository;
    private final VolunteerRepository  volunteerRepository;
    private final GodparentRepository  godparentRepository;
    private final ContactRepository    contactRepository;
    private final OngRepository        ongRepository;
    private final OngMembroRepository  membroRepository;

    public AdminDTOs.AdminStatsResponse stats() {
        Long ongId = TenantContext.get();

        if (ongId != null) {
            BigDecimal donations = donationRepository.sumConfirmedByOngId(ongId);
            return new AdminDTOs.AdminStatsResponse(
                animalRepository.countByOngId(ongId),
                animalRepository.countByOngIdAndStatus(ongId, Animal.AnimalStatus.DISPONIVEL),
                animalRepository.countByOngIdAndStatus(ongId, Animal.AnimalStatus.ADOTADO),
                userRepository.countByRole(User.Role.USER),
                adoptionRepository.countByAnimalOngId(ongId),
                adoptionRepository.countByAnimalOngIdAndStatus(ongId, Adoption.AdoptionStatus.PENDENTE),
                donations != null ? donations : BigDecimal.ZERO,
                volunteerRepository.countByOngIdAndStatus(ongId, Volunteer.VolunteerStatus.APROVADO),
                godparentRepository.countByAnimalOngIdAndStatus(ongId, Godparent.GodparentStatus.APROVADO),
                contactRepository.countByRead(false),
                0L, 0L, 0L
            );
        }

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
            contactRepository.countByRead(false),
            ongRepository.count(),
            ongRepository.countByStatus(Ong.OngStatus.PENDENTE),
            ongRepository.countByStatus(Ong.OngStatus.ATIVA)
        );
    }

    public List<AdminDTOs.UserAdminResponse> listarUsuarios() {
        Long ongId = TenantContext.get();
        if (ongId != null) {
            Ong ong = ongRepository.findById(ongId).orElseThrow();
            return membroRepository.findByOngOrderByJoinedAtDesc(ong).stream()
                .map(m -> toUserResponse(m.getUser()))
                .toList();
        }
        return userRepository.findAll().stream()
            .map(this::toUserResponse)
            .toList();
    }

    public AdminDTOs.UserAdminResponse alterarRole(Long userId, AdminDTOs.AlterarRoleRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        if (request.role() == User.Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não é permitido promover usuários para ADMIN por esta rota");
        }
        user.setRole(request.role());
        return toUserResponse(userRepository.save(user));
    }

    private AdminDTOs.UserAdminResponse toUserResponse(User u) {
        return new AdminDTOs.UserAdminResponse(u.getId(), u.getName(), u.getEmail(), u.getCity(), u.getRole(), u.getCreatedAt());
    }
}
