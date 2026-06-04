package br.com.conectapet.service;

import br.com.conectapet.dto.OccurrenceDTOs;
import br.com.conectapet.model.Occurrence;
import br.com.conectapet.model.User;
import br.com.conectapet.repository.OccurrenceRepository;
import br.com.conectapet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OccurrenceService {

    private final OccurrenceRepository occurrenceRepository;
    private final UserRepository       userRepository;

    @Transactional
    public OccurrenceDTOs.OccurrenceResponse report(OccurrenceDTOs.OccurrenceRequest req) {
        boolean isAnonymous = Boolean.TRUE.equals(req.anonymous());

        var builder = Occurrence.builder()
            .type(req.type())
            .titulo(req.titulo())
            .description(req.description().trim())
            .estado(req.estado())
            .cidade(req.cidade())
            .cep(req.cep())
            .complemento(req.complemento())
            .endereco(req.endereco())
            .animalDescription(req.animalDescription())
            .animalIdentification(req.animalIdentification())
            .reporterName(req.reporterName())
            .reporterEmail(req.reporterEmail())
            .anonymous(isAnonymous);

        // Sempre vincula ao usuário autenticado (anônimo afeta visibilidade, não o histórico)
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            userRepository.findByEmail(auth.getName()).ifPresent(builder::user);
        }

        return OccurrenceDTOs.OccurrenceResponse.from(occurrenceRepository.save(builder.build()));
    }

    public List<OccurrenceDTOs.OccurrenceResponse> list(Occurrence.OccurrenceStatus status) {
        boolean maskReporter = !currentUserIsAdmin();
        List<Occurrence> list = status != null
            ? occurrenceRepository.findByStatusOrderByCreatedAtDesc(status)
            : occurrenceRepository.findAllByOrderByCreatedAtDesc();
        return list.stream().map(o -> OccurrenceDTOs.OccurrenceResponse.from(o, maskReporter)).toList();
    }

    public List<OccurrenceDTOs.OccurrenceResponse> listarMinhas() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return occurrenceRepository.findByUserOrderByCreatedAtDesc(user)
            .stream().map(OccurrenceDTOs.OccurrenceResponse::from).toList();
    }

    @Transactional
    public OccurrenceDTOs.OccurrenceResponse updateStatus(Long id, OccurrenceDTOs.UpdateStatusRequest req) {
        Occurrence occ = occurrenceRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ocorrência não encontrada."));
        occ.setStatus(req.status());
        if (req.adminNotes() != null) occ.setAdminNotes(req.adminNotes().isBlank() ? null : req.adminNotes().trim());

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            userRepository.findByEmail(auth.getName()).ifPresent(gestor -> {
                occ.setAnalisadoPor(gestor);
                occ.setAnalisadoEm(java.time.LocalDateTime.now());
            });
        }

        return OccurrenceDTOs.OccurrenceResponse.from(occurrenceRepository.save(occ));
    }

    @Transactional
    public void delete(Long id) {
        if (!occurrenceRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ocorrência não encontrada.");
        occurrenceRepository.deleteById(id);
    }

    private boolean currentUserIsAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
