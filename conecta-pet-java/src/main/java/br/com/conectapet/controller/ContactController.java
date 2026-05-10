package br.com.conectapet.controller;

import br.com.conectapet.dto.ContactDTOs;
import br.com.conectapet.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@Tag(name = "Contato", description = "Endpoints para gerenciar mensagens de contato")
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    @Operation(summary = "Enviar mensagem de contato", description = "Cria uma nova mensagem de contato (pública)")
    @ApiResponse(responseCode = "201", description = "Mensagem enviada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<ContactDTOs.ContactResponse> send(
            @Valid @RequestBody ContactDTOs.ContactRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactService.send(req));
    }

    @GetMapping
    @Operation(summary = "Listar mensagens de contato", description = "Retorna todas as mensagens recebidas (requer ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Lista de mensagens retornada com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)")
    public ResponseEntity<List<ContactDTOs.ContactResponse>> listAll() {
        return ResponseEntity.ok(contactService.listAll());
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Marcar mensagem como lida", description = "Atualiza o status de uma mensagem para lida (requer ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Mensagem marcada como lida com sucesso")
    @ApiResponse(responseCode = "404", description = "Mensagem não encontrada")
    @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "ID da mensagem") @PathVariable Long id) {
        contactService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}
