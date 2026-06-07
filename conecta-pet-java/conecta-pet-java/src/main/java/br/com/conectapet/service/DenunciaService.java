package br.com.conectapet.service;

import br.com.conectapet.config.TenantContext;
import br.com.conectapet.dto.DenunciaDTOs;
import br.com.conectapet.model.Denuncia;
import br.com.conectapet.model.Ong;
import br.com.conectapet.model.User;
import br.com.conectapet.repository.DenunciaRepository;
import br.com.conectapet.repository.OngRepository;
import br.com.conectapet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DenunciaService {

    private final DenunciaRepository denunciaRepository;
    private final UserRepository     userRepository;
    private final OngRepository      ongRepository;

    public DenunciaDTOs.DenunciaResponse criar(DenunciaDTOs.CriarDenunciaRequest request) {
        User user = usuarioAutenticado();
        var builder = Denuncia.builder()
            .user(user)
            .titulo(request.titulo())
            .descricao(request.descricao())
            .estado(request.estado())
            .cidade(request.cidade())
            .cep(request.cep())
            .complemento(request.complemento())
            .endereco(request.endereco())
            .categoria(request.categoria());

        Long ongId = TenantContext.get();
        if (ongId != null) {
            ongRepository.findById(ongId).ifPresent(builder::ong);
        }

        return toResponse(denunciaRepository.save(builder.build()));
    }

    public List<DenunciaDTOs.DenunciaResponse> listarTodas(Denuncia.StatusDenuncia status) {
        Long ongId = TenantContext.get();
        List<Denuncia> denuncias;

        if (ongId != null) {
            denuncias = status != null
                ? denunciaRepository.findByOngIdAndStatusOrderByCreatedAtDesc(ongId, status)
                : denunciaRepository.findByOngIdOrderByCreatedAtDesc(ongId);
        } else {
            denuncias = status != null
                ? denunciaRepository.findByStatusOrderByCreatedAtDesc(status)
                : denunciaRepository.findAllByOrderByCreatedAtDesc();
        }

        return denuncias.stream().map(this::toResponse).toList();
    }

    public List<DenunciaDTOs.DenunciaResponse> listarMinhas() {
        User user = usuarioAutenticado();
        return denunciaRepository.findByUserOrderByCreatedAtDesc(user)
            .stream().map(this::toResponse).toList();
    }

    public DenunciaDTOs.DenunciaResponse buscarPorId(Long id) {
        Denuncia denuncia = denunciaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Denúncia não encontrada"));
        User user = usuarioAutenticado();
        boolean isGestorOuAdmin = user.getRole() == User.Role.ADMIN || user.getRole() == User.Role.GESTOR_PUBLICO;
        if (!isGestorOuAdmin && !denuncia.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
        return toResponse(denuncia);
    }

    public DenunciaDTOs.DenunciaResponse atualizarStatus(Long id, DenunciaDTOs.AtualizarStatusRequest request) {
        Denuncia denuncia = denunciaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Denúncia não encontrada"));
        User gestor = usuarioAutenticado();
        denuncia.setStatus(request.status());
        if (request.observacaoGestor() != null) {
            denuncia.setObservacaoGestor(request.observacaoGestor());
        }
        denuncia.setAnalisadoPor(gestor);
        denuncia.setAnalisadoEm(LocalDateTime.now());
        return toResponse(denunciaRepository.save(denuncia));
    }

    private User usuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado"));
    }

    private DenunciaDTOs.DenunciaResponse toResponse(Denuncia d) {
        return new DenunciaDTOs.DenunciaResponse(
            d.getId(),
            d.getUser().getId(),
            d.getUser().getName(),
            d.getTitulo(),
            d.getDescricao(),
            d.getEstado(),
            d.getCidade(),
            d.getCep(),
            d.getComplemento(),
            d.getEndereco(),
            d.getCategoria(),
            d.getStatus(),
            d.getObservacaoGestor(),
            d.getAnalisadoPor() != null ? d.getAnalisadoPor().getName() : null,
            d.getAnalisadoEm(),
            d.getCreatedAt(),
            d.getUpdatedAt()
        );
    }
}
