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
    @Operation(summary = "Apadrinhar um animal", description = "Cria um novo apadrinhamamiento para um usuário")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Apadrinhamamiento criado com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<GodparentDTOs.GodparentResponse> register(
            @Valid @RequestBody GodparentDTOs.GodparentRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(godparentService.register(req, principal.getUsername()));
    }

    @GetMapping("/mine")
    @Operation(summary = "Listar meus apadrinhamientos", description = "Retorna todos os apadrinhamientos do usuário autenticado")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Lista de apadrinhamientos retornada com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    public ResponseEntity<List<GodparentDTOs.GodparentResponse>> mine(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(godparentService.myGodparents(principal.getUsername()));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Desativar apadrinhamamiento", description = "Altera o status do apadrinhamamiento para INATIVO")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Apadrinhamamiento não encontrado")
    @ApiResponse(responseCode = "403", description = "Acesso negado (apenas o proprietário pode desativar)")
    public ResponseEntity<GodparentDTOs.GodparentResponse> updateStatus(
            @Parameter(description = "ID do apadrinhamamiento") @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(godparentService.deactivateGodparent(id, principal.getUsername()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar apadrinhamamiento", description = "Remove um apadrinhamamiento (apenas o proprietário)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Apadrinhamamiento deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Apadrinhamamiento não encontrado")
    @ApiResponse(responseCode = "403", description = "Acesso negado (apenas o proprietário pode deletar)")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do apadrinhamamiento") @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        godparentService.deleteGodparent(id, principal.getUsername());
        return ResponseEntity.noContent().build();
    }
}
