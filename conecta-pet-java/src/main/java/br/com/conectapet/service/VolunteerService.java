package br.com.conectapet.service;

import br.com.conectapet.config.TenantContext;
import br.com.conectapet.dto.VolunteerDTOs;
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
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final UserRepository      userRepository;
    private final OngRepository       ongRepository;

    @Transactional
    public VolunteerDTOs.VolunteerResponse register(VolunteerDTOs.VolunteerRequest req, String userEmail) {
        var user = findUser(userEmail);
        if (volunteerRepository.existsByUser(user))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já está cadastrado como voluntário.");

        var volBuilder = Volunteer.builder()
            .user(user)
            .skills(req.skills())
            .availability(req.availability())
            .motivation(req.motivation());

        Long ongId = TenantContext.get();
        if (ongId != null) {
            ongRepository.findById(ongId).ifPresent(volBuilder::ong);
        }

        return VolunteerDTOs.VolunteerResponse.from(volunteerRepository.save(volBuilder.build()));
    }

    public VolunteerDTOs.VolunteerResponse myVolunteer(String userEmail) {
        return volunteerRepository.findByUser(findUser(userEmail))
            .map(VolunteerDTOs.VolunteerResponse::from)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Não encontrado."));
    }

   @Transactional
    public VolunteerDTOs.VolunteerResponse update(Long id, VolunteerDTOs.VolunteerRequest req, String userEmail) {
    Volunteer volunteer = volunteerRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voluntário não encontrado."));

    User user = findUser(userEmail);
    if (!volunteer.getUser().getId().equals(user.getId())) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para atualizar este voluntário.");
    }

    volunteer.setSkills(req.skills());
    volunteer.setAvailability(req.availability());
    volunteer.setMotivation(req.motivation());
    volunteer.setStatus(Volunteer.VolunteerStatus.PENDENTE);

    return VolunteerDTOs.VolunteerResponse.from(volunteerRepository.save(volunteer));
}

    @Transactional
    public VolunteerDTOs.VolunteerResponse approve(Long id) {
        Volunteer volunteer = volunteerRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voluntário não encontrado."));

        volunteer.setStatus(Volunteer.VolunteerStatus.APROVADO);
        return VolunteerDTOs.VolunteerResponse.from(volunteerRepository.save(volunteer));
    }

    @Transactional
    public VolunteerDTOs.VolunteerResponse reject(Long id) {
        Volunteer volunteer = volunteerRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voluntário não encontrado."));

        volunteer.setStatus(Volunteer.VolunteerStatus.REJEITADO);
        return VolunteerDTOs.VolunteerResponse.from(volunteerRepository.save(volunteer));
    }

    public List<VolunteerDTOs.VolunteerResponse> listAll() {
        Long ongId = TenantContext.get();
        List<Volunteer> volunteers = ongId != null
            ? volunteerRepository.findByOngIdOrderByCreatedAtDesc(ongId)
            : volunteerRepository.findAllByOrderByCreatedAtDesc();
        return volunteers.stream().map(VolunteerDTOs.VolunteerResponse::from).toList();
    }

    @Transactional
    public void deleteBySkillType(String skillType) {
        var volunteers = volunteerRepository.findBySkillsContaining(skillType);
        if (volunteers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Nenhum voluntário encontrado com o tipo: " + skillType);
        }
        volunteerRepository.deleteAll(volunteers);
    }

    @Transactional
    public VolunteerDTOs.VolunteerResponse deleteById(Long id, String userEmail) {
        Volunteer volunteer = volunteerRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voluntário não encontrado."));

        User user = findUser(userEmail);
        if (!volunteer.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para deletar este voluntário.");
        }

        var response = VolunteerDTOs.VolunteerResponse.from(volunteer);
        volunteerRepository.deleteById(id);
        return response;
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
    }
}
