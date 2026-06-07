package br.com.conectapet.controller;

import br.com.conectapet.dto.DenunciaDTOs;
import br.com.conectapet.model.Denuncia;
import br.com.conectapet.service.DenunciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/denuncias")
@RequiredArgsConstructor
@Tag(name = "Denúncias", description = "Gestão de denúncias de maus-tratos e abandono de animais")
@SecurityRequirement(name = "bearerAuth")
public class DenunciaController {

    private final DenunciaService denunciaService;

    @PostMapping
    @Operation(summary = "Registrar denúncia", description = "Qualquer usuário autenticado pode registrar uma denúncia")
    @ApiResponse(responseCode = "201", description = "Denúncia registrada com sucesso")
    public ResponseEntity<DenunciaDTOs.DenunciaResponse> criar(
            @Valid @RequestBody DenunciaDTOs.CriarDenunciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(denunciaService.criar(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR_PUBLICO')")
    @Operation(summary = "Listar todas as denúncias", description = "Acessível por ADMIN e GESTOR_PUBLICO")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<DenunciaDTOs.DenunciaResponse>> listarTodas(
            @RequestParam(required = false) Denuncia.StatusDenuncia status) {
        return ResponseEntity.ok(denunciaService.listarTodas(status));
    }

    @GetMapping("/mine")
    @Operation(summary = "Listar minhas denúncias", description = "Retorna as denúncias do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<DenunciaDTOs.DenunciaResponse>> listarMinhas() {
        return ResponseEntity.ok(denunciaService.listarMinhas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar denúncia por ID", description = "Dono da denúncia, ADMIN ou GESTOR_PUBLICO podem acessar")
    @ApiResponse(responseCode = "200", description = "Denúncia encontrada")
    @ApiResponse(responseCode = "404", description = "Denúncia não encontrada")
    @ApiResponse(responseCode = "403", description = "Acesso negado")
    public ResponseEntity<DenunciaDTOs.DenunciaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(denunciaService.buscarPorId(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR_PUBLICO')")
    @Operation(summary = "Atualizar status da denúncia", description = "ADMIN e GESTOR_PUBLICO podem atualizar o status e adicionar observações")
    @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Denúncia não encontrada")
    public ResponseEntity<DenunciaDTOs.DenunciaResponse> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody DenunciaDTOs.AtualizarStatusRequest request) {
        return ResponseEntity.ok(denunciaService.atualizarStatus(id, request));
    }
}
