package br.com.conectapet.controller;

import br.com.conectapet.dto.AdminDTOs;
import br.com.conectapet.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Endpoints administrativos do sistema")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    @Operation(summary = "Obter estatísticas do sistema", description = "Retorna estatísticas gerais do sistema (requer ADMIN)")
    @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)")
    public ResponseEntity<AdminDTOs.AdminStatsResponse> stats() {
        return ResponseEntity.ok(adminService.stats());
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários cadastrados (requer ADMIN)")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<AdminDTOs.UserAdminResponse>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarUsuarios());
    }

    @PatchMapping("/usuarios/{id}/role")
    @Operation(
        summary = "Alterar perfil de usuário",
        description = "Permite ao ADMIN atribuir o perfil GESTOR_PUBLICO (ou revogar de volta para USER). Não é possível promover para ADMIN por esta rota."
    )
    @ApiResponse(responseCode = "200", description = "Perfil alterado com sucesso")
    @ApiResponse(responseCode = "403", description = "Tentativa de promover para ADMIN negada")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    public ResponseEntity<AdminDTOs.UserAdminResponse> alterarRole(
            @PathVariable Long id,
            @Valid @RequestBody AdminDTOs.AlterarRoleRequest request) {
        return ResponseEntity.ok(adminService.alterarRole(id, request));
    }
}
