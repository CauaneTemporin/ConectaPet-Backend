package br.com.conectapet.controller;

import br.com.conectapet.dto.AdoptionDTOs;
import br.com.conectapet.service.AdoptionService;
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
@RequestMapping("/api/adoptions")
@RequiredArgsConstructor
@Tag(name = "Adoções", description = "Endpoints para gerenciar adoções de animais")
public class AdoptionController {

    private final AdoptionService adoptionService;

    @PostMapping
    @Operation(summary = "Solicitar adoção", description = "Usuário autenticado solicita a adoção de um animal")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Solicitação de adoção criada com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<AdoptionDTOs.AdoptionResponse> request(
            @Valid @RequestBody AdoptionDTOs.AdoptionRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(adoptionService.request(req, principal.getUsername()));
    }

    @GetMapping("/mine")
    @Operation(summary = "Minhas adoções", description = "Retorna todas as adoções do usuário autenticado")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Lista de adoções retornada com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    public ResponseEntity<List<AdoptionDTOs.AdoptionResponse>> mine(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(adoptionService.myAdoptions(principal.getUsername()));
    }

    @GetMapping
    @Operation(summary = "Listar todas as adoções", description = "Lista todas as solicitações de adoção com filtro de status (requer ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Lista de adoções retornada com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)")
    public ResponseEntity<List<AdoptionDTOs.AdoptionResponse>> listAll(
            @Parameter(description = "Filtrar por status: PENDENTE, APROVADO, RECUSADO ou CONCLUIDO") @RequestParam(required = false) String status) {
        return ResponseEntity.ok(adoptionService.listAll(status));
    }

    @PatchMapping("/{id}/review")
    @Operation(summary = "Revisar adoção", description = "Admin aprova, recusa ou conclui uma solicitação de adoção")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Adoção revisada com sucesso")
    @ApiResponse(responseCode = "404", description = "Adoção não encontrada")
    @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)")
    public ResponseEntity<AdoptionDTOs.AdoptionResponse> review(
            @Parameter(description = "ID da adoção") @PathVariable Long id,
            @Valid @RequestBody AdoptionDTOs.ReviewRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(
            adoptionService.review(id, req, principal.getUsername()));
    }
}
