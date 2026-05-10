package br.com.conectapet.controller;

import br.com.conectapet.dto.AdminDTOs;
import br.com.conectapet.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Endpoints administrativos do sistema")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    @Operation(summary = "Obter estatísticas do sistema", description = "Retorna estatísticas gerais do sistema (requer ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)")
    public ResponseEntity<AdminDTOs.AdminStatsResponse> stats() {
        return ResponseEntity.ok(adminService.stats());
    }
}
