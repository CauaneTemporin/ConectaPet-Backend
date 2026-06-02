package br.com.conectapet.controller;

import br.com.conectapet.dto.OccurrenceDTOs;
import br.com.conectapet.model.Occurrence;
import br.com.conectapet.service.OccurrenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/occurrences")
@RequiredArgsConstructor
@Tag(name = "Ocorrências", description = "Registro público de ocorrências de abandono ou maus-tratos")
public class OccurrenceController {

    private final OccurrenceService occurrenceService;

    @PostMapping
    @Operation(summary = "Registrar ocorrência", description = "Qualquer pessoa pode registrar, sem autenticação obrigatória")
    public ResponseEntity<OccurrenceDTOs.OccurrenceResponse> report(
            @Valid @RequestBody OccurrenceDTOs.OccurrenceRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(occurrenceService.report(req));
    }

    @GetMapping("/mine")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Minhas ocorrências", description = "Retorna as ocorrências do usuário autenticado")
    public ResponseEntity<List<OccurrenceDTOs.OccurrenceResponse>> mine() {
        return ResponseEntity.ok(occurrenceService.listarMinhas());
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar ocorrências", description = "Requer ADMIN, ONG_ADMIN ou GESTOR_PUBLICO")
    public ResponseEntity<List<OccurrenceDTOs.OccurrenceResponse>> list(
            @RequestParam(required = false) Occurrence.OccurrenceStatus status) {
        return ResponseEntity.ok(occurrenceService.list(status));
    }

    @PatchMapping("/{id}/status")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualizar status da ocorrência")
    public ResponseEntity<OccurrenceDTOs.OccurrenceResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OccurrenceDTOs.UpdateStatusRequest req) {
        return ResponseEntity.ok(occurrenceService.updateStatus(id, req));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Deletar ocorrência")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        occurrenceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
