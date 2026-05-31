package br.com.conectapet.service;

import br.com.conectapet.dto.OngDTOs;
import br.com.conectapet.model.*;
import br.com.conectapet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OngService {

    private final OngRepository         ongRepository;
    private final OngMembroRepository   membroRepository;
    private final UserRepository        userRepository;

    // ── Público ──────────────────────────────────────────────────────────────

    public List<OngDTOs.OngResumoResponse> listarAtivas() {
        return ongRepository.findByStatusOrderByNomeFantasiaAsc(Ong.OngStatus.ATIVA)
            .stream().map(this::toResumo).toList();
    }

    public OngDTOs.OngResponse buscarPorId(Long id) {
        return toResponse(buscarOng(id));
    }

    // ── Autenticado ───────────────────────────────────────────────────────────

    @Transactional
    public OngDTOs.OngResponse criar(OngDTOs.CriarOngRequest request) {
        if (ongRepository.existsByCnpj(request.cnpj())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CNPJ já cadastrado");
        }
        User solicitante = usuarioAutenticadoOpcional();
        Ong ong = Ong.builder()
            .cnpj(request.cnpj())
            .razaoSocial(request.razaoSocial())
            .nomeFantasia(request.nomeFantasia())
            .email(request.email())
            .telefone(request.telefone())
            .endereco(request.endereco())
            .cidade(request.cidade())
            .estado(request.estado().toUpperCase())
            .descricao(request.descricao())
            .status(Ong.OngStatus.ATIVA)
            .solicitadoPor(solicitante)
            .build();
        Ong saved = ongRepository.save(ong);

        // Criador vira ONG_ADMIN automaticamente
        if (solicitante != null && !membroRepository.existsByOngAndUser(saved, solicitante)) {
            membroRepository.save(OngMembro.builder()
                .ong(saved).user(solicitante)
                .role(OngMembro.OngMembroRole.ONG_ADMIN)
                .build());
        }

        return toResponse(saved);
    }

    @Transactional
    public OngDTOs.OngMembroResponse aderir(Long ongId) {
        Ong ong = buscarOngAtiva(ongId);
        User user = usuarioAutenticado();

        if (membroRepository.existsByOngAndUser(ong, user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já é membro desta ONG");
        }
        OngMembro membro = OngMembro.builder().ong(ong).user(user).build();
        return toMembroResponse(membroRepository.save(membro));
    }

    @Transactional
    public void sair(Long ongId) {
        Ong ong = buscarOng(ongId);
        User user = usuarioAutenticado();
        OngMembro membro = membroRepository.findByOngAndUser(ong, user)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Você não é membro desta ONG"));

        if (membro.getRole() == OngMembro.OngMembroRole.ONG_ADMIN) {
            long admins = membroRepository.findByOngOrderByJoinedAtDesc(ong).stream()
                .filter(m -> m.getRole() == OngMembro.OngMembroRole.ONG_ADMIN).count();
            if (admins <= 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Você é o único administrador. Transfira o papel antes de sair.");
            }
        }
        membroRepository.delete(membro);
    }

    public List<OngDTOs.OngResumoResponse> minhasOngs() {
        User user = usuarioAutenticado();
        return membroRepository.findByUserOrderByJoinedAtDesc(user)
            .stream()
            .map(m -> toResumoComRole(m.getOng(), m.getRole()))
            .toList();
    }

    // ── ONG Admin ─────────────────────────────────────────────────────────────

    public List<OngDTOs.OngMembroResponse> listarMembros(Long ongId) {
        Ong ong = buscarOng(ongId);
        User user = usuarioAutenticado();
        verificarOngAdmin(ong, user);
        return membroRepository.findByOngOrderByJoinedAtDesc(ong)
            .stream().map(this::toMembroResponse).toList();
    }

    @Transactional
    public OngDTOs.OngMembroResponse alterarRoleMembro(Long ongId, Long userId, OngDTOs.AlterarMembroRoleRequest request) {
        Ong ong = buscarOng(ongId);
        User currentUser = usuarioAutenticado();
        verificarOngAdmin(ong, currentUser);

        User alvo = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (alvo.getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você não pode alterar seu próprio papel");
        }

        OngMembro membro = membroRepository.findByOngAndUser(ong, alvo)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não é membro desta ONG"));
        membro.setRole(request.role());
        return toMembroResponse(membroRepository.save(membro));
    }

    @Transactional
    public OngDTOs.OngMembroResponse adicionarMembro(Long ongId, Long userId) {
        Ong ong = buscarOngAtiva(ongId);
        User currentUser = usuarioAutenticado();
        verificarOngAdmin(ong, currentUser);

        User novoMembro = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (membroRepository.existsByOngAndUser(ong, novoMembro)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já é membro desta ONG");
        }
        OngMembro membro = OngMembro.builder().ong(ong).user(novoMembro).build();
        return toMembroResponse(membroRepository.save(membro));
    }

    @Transactional
    public void removerMembro(Long ongId, Long userId) {
        Ong ong = buscarOng(ongId);
        User currentUser = usuarioAutenticado();
        verificarOngAdmin(ong, currentUser);

        User alvo = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        OngMembro membro = membroRepository.findByOngAndUser(ong, alvo)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não é membro desta ONG"));
        membroRepository.delete(membro);
    }

    // ── Auxiliares ────────────────────────────────────────────────────────────

    private Ong buscarOng(Long id) {
        return ongRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ONG não encontrada"));
    }

    private Ong buscarOngAtiva(Long id) {
        Ong ong = buscarOng(id);
        if (ong.getStatus() != Ong.OngStatus.ATIVA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ONG não está ativa");
        }
        return ong;
    }

    private User usuarioAutenticadoOpcional() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    private User usuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado"));
    }

    private void verificarOngAdmin(Ong ong, User user) {
        if (user.getRole() == User.Role.ADMIN) return;
        boolean isAdmin = membroRepository.existsByOngAndUserAndRole(ong, user, OngMembro.OngMembroRole.ONG_ADMIN);
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso restrito ao administrador da ONG");
        }
    }

    private OngDTOs.OngResumoResponse toResumo(Ong o) {
        return new OngDTOs.OngResumoResponse(
            o.getId(), o.getNomeFantasia(), o.getRazaoSocial(),
            o.getCidade(), o.getEstado(), o.getDescricao(), o.getLogoUrl(), o.getStatus(), null
        );
    }

    private OngDTOs.OngResumoResponse toResumoComRole(Ong o, OngMembro.OngMembroRole role) {
        return new OngDTOs.OngResumoResponse(
            o.getId(), o.getNomeFantasia(), o.getRazaoSocial(),
            o.getCidade(), o.getEstado(), o.getDescricao(), o.getLogoUrl(), o.getStatus(), role
        );
    }

    private OngDTOs.OngResponse toResponse(Ong o) {
        return new OngDTOs.OngResponse(
            o.getId(), o.getCnpj(), o.getRazaoSocial(), o.getNomeFantasia(),
            o.getEmail(), o.getTelefone(), o.getEndereco(), o.getCidade(), o.getEstado(),
            o.getDescricao(), o.getLogoUrl(), o.getStatus(),
            o.getSolicitadoPor() != null ? o.getSolicitadoPor().getId() : null,
            o.getSolicitadoPor() != null ? o.getSolicitadoPor().getName() : null,
            o.getCreatedAt(), o.getUpdatedAt()
        );
    }

    private OngDTOs.OngMembroResponse toMembroResponse(OngMembro m) {
        return new OngDTOs.OngMembroResponse(
            m.getId(), m.getUser().getId(), m.getUser().getName(),
            m.getUser().getEmail(), m.getRole(), m.getJoinedAt()
        );
    }
}
