package br.com.conectapet.controller;

import br.com.conectapet.dto.OngDTOs;
import br.com.conectapet.service.OngService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ongs")
@RequiredArgsConstructor
@Tag(name = "ONGs", description = "Cadastro e gestão de ONGs parceiras")
public class OngController {

    private final OngService ongService;

    // ── Público ──────────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Listar ONGs ativas", description = "Retorna todas as ONGs com status ATIVA")
    public ResponseEntity<List<OngDTOs.OngResumoResponse>> listarAtivas() {
        return ResponseEntity.ok(ongService.listarAtivas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ONG por ID")
    public ResponseEntity<OngDTOs.OngResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ongService.buscarPorId(id));
    }

    // ── Autenticado ───────────────────────────────────────────────────────────

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cadastrar nova ONG", description = "Solicita o cadastro de uma nova ONG (fica pendente até aprovação do admin)")
    public ResponseEntity<OngDTOs.OngResponse> criar(@Valid @RequestBody OngDTOs.CriarOngRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ongService.criar(request));
    }

    @PostMapping("/{id}/aderir")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Aderir à ONG", description = "Usuário autenticado passa a ser membro da ONG")
    public ResponseEntity<OngDTOs.OngMembroResponse> aderir(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ongService.aderir(id));
    }

    @DeleteMapping("/{id}/aderir")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Sair da ONG")
    public ResponseEntity<Void> sair(@PathVariable Long id) {
        ongService.sair(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/minhas")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Minhas ONGs", description = "Retorna as ONGs das quais o usuário autenticado é membro")
    public ResponseEntity<List<OngDTOs.OngResumoResponse>> minhasOngs() {
        return ResponseEntity.ok(ongService.minhasOngs());
    }

    // ── ONG Admin ─────────────────────────────────────────────────────────────

    @GetMapping("/{id}/membros")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar membros da ONG", description = "Requer papel ONG_ADMIN ou ADMIN do sistema")
    public ResponseEntity<List<OngDTOs.OngMembroResponse>> listarMembros(@PathVariable Long id) {
        return ResponseEntity.ok(ongService.listarMembros(id));
    }

    @PostMapping("/{id}/membros/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Adicionar membro à ONG")
    public ResponseEntity<OngDTOs.OngMembroResponse> adicionarMembro(
            @PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ongService.adicionarMembro(id, userId));
    }

    @DeleteMapping("/{id}/membros/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remover membro da ONG")
    public ResponseEntity<Void> removerMembro(
            @PathVariable Long id, @PathVariable Long userId) {
        ongService.removerMembro(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/perfil")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualizar perfil da ONG", description = "ONG admin pode editar história, missão e informações de contato")
    public ResponseEntity<OngDTOs.OngResponse> atualizarPerfil(
            @PathVariable Long id,
            @RequestBody OngDTOs.AtualizarPerfilOngRequest request) {
        return ResponseEntity.ok(ongService.atualizarPerfil(id, request));
    }

    @PatchMapping("/{id}/membros/{userId}/aprovar")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Aprovar solicitação de membro")
    public ResponseEntity<OngDTOs.OngMembroResponse> aprovarMembro(
            @PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(ongService.aprovarMembro(id, userId));
    }

    @PatchMapping("/{id}/membros/{userId}/rejeitar")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Rejeitar solicitação de membro")
    public ResponseEntity<OngDTOs.OngMembroResponse> rejeitarMembro(
            @PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(ongService.rejeitarMembro(id, userId));
    }

    @PatchMapping("/{id}/membros/{userId}/role")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Alterar papel de membro")
    public ResponseEntity<OngDTOs.OngMembroResponse> alterarRoleMembro(
            @PathVariable Long id,
            @PathVariable Long userId,
            @Valid @RequestBody OngDTOs.AlterarMembroRoleRequest request) {
        return ResponseEntity.ok(ongService.alterarRoleMembro(id, userId, request));
    }

}
