package br.com.conectapet.controller;

import br.com.conectapet.dto.AnimalDTOs;
import br.com.conectapet.service.AnimalService;
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
@RequestMapping("/api/animals")
@RequiredArgsConstructor
@Tag(name = "Animais", description = "Endpoints para gerenciar animais disponíveis para adoção")
public class AnimalController {

    private final AnimalService animalService;

    @GetMapping
    @Operation(summary = "Listar animais", description = "Lista todos os animais com filtros opcionais")
    @ApiResponse(responseCode = "200", description = "Lista de animais retornada com sucesso")
    public ResponseEntity<List<AnimalDTOs.AnimalResponse>> list(
            @Parameter(description = "Espécie do animal (cachorro, gato, etc)") @RequestParam(required = false) String species,
            @Parameter(description = "Tamanho do animal (pequeno, medio, grande)") @RequestParam(required = false) String size,
            @Parameter(description = "Status do animal (disponivel, adotado, apadrinhado)") @RequestParam(required = false) String status,
            @Parameter(description = "Busca textual por nome, raça ou descrição") @RequestParam(required = false) String q) {
        return ResponseEntity.ok(animalService.list(species, size, status, q));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter animal por ID", description = "Retorna detalhes de um animal específico")
    @ApiResponse(responseCode = "200", description = "Animal encontrado")
    @ApiResponse(responseCode = "404", description = "Animal não encontrado")
    public ResponseEntity<AnimalDTOs.AnimalResponse> findById(
            @Parameter(description = "ID do animal") @PathVariable Long id) {
        return ResponseEntity.ok(animalService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo animal", description = "Cria um novo registro de animal (requer ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Animal criado com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)")
    public ResponseEntity<AnimalDTOs.AnimalResponse> create(
            @Valid @RequestBody AnimalDTOs.AnimalRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(animalService.create(req, principal.getUsername()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar animal completamente", description = "Atualiza todos os campos de um animal (requer ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Animal atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Animal não encontrado")
    @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)")
    public ResponseEntity<AnimalDTOs.AnimalResponse> update(
            @Parameter(description = "ID do animal") @PathVariable Long id,
            @Valid @RequestBody AnimalDTOs.AnimalUpdateRequest req) {
        return ResponseEntity.ok(animalService.update(id, req));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar animal parcialmente", description = "Atualiza apenas os campos informados (requer ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Animal atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Animal não encontrado")
    @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)")
    public ResponseEntity<AnimalDTOs.AnimalResponse> patch(
            @Parameter(description = "ID do animal") @PathVariable Long id,
            @RequestBody AnimalDTOs.AnimalUpdateRequest req) {
        return ResponseEntity.ok(animalService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar animal", description = "Remove um animal do sistema (requer ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Animal deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Animal não encontrado")
    @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do animal") @PathVariable Long id) {
        animalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
