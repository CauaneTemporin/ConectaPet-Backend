package br.com.conectapet.controller;

import br.com.conectapet.dto.DonationDTOs;
import br.com.conectapet.service.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
@Tag(name = "Doações", description = "Endpoints para gerenciar doações financeiras")
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    @Operation(summary = "Fazer uma doação", description = "Cria um novo registro de doação (pode ser anônimo)")
    @ApiResponse(responseCode = "201", description = "Doação registrada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<DonationDTOs.DonationResponse> donate(
            @Valid @RequestBody DonationDTOs.DonationRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        String email = principal != null ? principal.getUsername() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(donationService.donate(req, email));
    }

    @GetMapping("/mine")
    @Operation(summary = "Minhas doações", description = "Retorna todas as doações do usuário autenticado")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Lista de doações retornada com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    public ResponseEntity<List<DonationDTOs.DonationResponse>> mine(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(donationService.myDonations(principal.getUsername()));
    }

    @GetMapping("/stats")
    @Operation(summary = "Estatísticas de doações", description = "Retorna estatísticas gerais de doações (total, média, quantidade)")
    @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso")
    public ResponseEntity<DonationDTOs.DonationStatsResponse> stats() {
        return ResponseEntity.ok(donationService.stats());
    }

    @GetMapping
    @Operation(summary = "Listar todas as doações", description = "Retorna todas as doações registradas (requer ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Lista de doações retornada com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)")
    public ResponseEntity<List<DonationDTOs.DonationResponse>> listAll() {
        return ResponseEntity.ok(donationService.listAll());
    }
}
