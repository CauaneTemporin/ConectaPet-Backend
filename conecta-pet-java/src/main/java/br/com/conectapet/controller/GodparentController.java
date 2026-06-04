package br.com.conectapet.controller;

import br.com.conectapet.dto.GodparentDTOs;
import br.com.conectapet.service.GodparentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/godparents")
@RequiredArgsConstructor
@Tag(name = "Apadrinhamientos", description = "Endpoints para gerenciar apadrinhamientos de animais")
public class GodparentController {

    private final GodparentService godparentService;

    @PostMapping
    @Operation(summary = "Apadrinhar um animal")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Apadrinhamamiento criado com sucesso")
    public ResponseEntity<GodparentDTOs.GodparentResponse> register(
            @Valid @RequestBody GodparentDTOs.GodparentRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(godparentService.register(req, principal.getUsername()));
    }

    @GetMapping("/mine")
    @Operation(summary = "Listar meus apadrinhamentos")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<GodparentDTOs.GodparentResponse>> mine(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(godparentService.myGodparents(principal.getUsername()));
    }

    @PatchMapping("/{id}/amount")
    @Operation(summary = "Alterar valor do apadrinhamento")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<GodparentDTOs.GodparentResponse> updateAmount(
            @PathVariable Long id,
            @Valid @RequestBody GodparentDTOs.AmountRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(godparentService.updateAmount(id, req, principal.getUsername()));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Desativar apadrinhamento")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<GodparentDTOs.GodparentResponse> updateStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(godparentService.deactivateGodparent(id, principal.getUsername()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar apadrinhamento (proprietário)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        godparentService.deleteGodparent(id, principal.getUsername());
        return ResponseEntity.noContent().build();
    }

    // ── Admin endpoints ────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Listar todos os apadrinhamentos (admin)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<GodparentDTOs.GodparentResponse>> listAll(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(godparentService.listAll(status));
    }

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Aprovar apadrinhamento (admin)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<GodparentDTOs.GodparentResponse> approve(
            @PathVariable Long id) {
        return ResponseEntity.ok(godparentService.approveGodparent(id));
    }

    @DeleteMapping("/{id}/admin")
    @Operation(summary = "Remover apadrinhamento (admin)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> adminDelete(
            @PathVariable Long id) {
        godparentService.adminDeleteGodparent(id);
        return ResponseEntity.noContent().build();
    }
}
