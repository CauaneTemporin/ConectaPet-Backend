package br.com.conectapet.controller;

import br.com.conectapet.dto.ShelterDTOs;
import br.com.conectapet.service.ShelterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shelters")
@RequiredArgsConstructor
@Tag(name = "Abrigos", description = "Endpoints para gerenciar abrigos de animais")
public class ShelterController {

    private final ShelterService shelterService;

    @GetMapping
    @Operation(summary = "Listar abrigos", description = "Retorna todos os abrigos cadastrados (público)")
    @ApiResponse(responseCode = "200", description = "Lista de abrigos retornada com sucesso")
    public ResponseEntity<List<ShelterDTOs.ShelterResponse>> listAll() {
        return ResponseEntity.ok(shelterService.listAll());
    }

    @PostMapping
    @Operation(summary = "Criar novo abrigo", description = "Cria um novo abrigo (requer ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Abrigo criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)")
    public ResponseEntity<ShelterDTOs.ShelterResponse> create(
            @Valid @RequestBody ShelterDTOs.ShelterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shelterService.create(req));
    }
}
