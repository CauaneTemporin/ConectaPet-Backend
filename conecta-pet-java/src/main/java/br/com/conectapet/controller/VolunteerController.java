package br.com.conectapet.controller;

import br.com.conectapet.dto.VolunteerDTOs;
import br.com.conectapet.service.VolunteerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/volunteers")
@RequiredArgsConstructor
@Tag(name = "Voluntários", description = "Endpoints para gerenciar voluntários")
public class VolunteerController {

    private final VolunteerService volunteerService;

    @PostMapping
    @Operation(summary = "Registrar como voluntário", description = "Permite que um usuário se registre como voluntário")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Voluntário registrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    public ResponseEntity<VolunteerDTOs.VolunteerResponse> register(
            @RequestBody VolunteerDTOs.VolunteerRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(volunteerService.register(req, principal.getUsername()));
    }

    @GetMapping("/mine")
    @Operation(summary = "Obter meu perfil de voluntário", description = "Retorna informações do perfil de voluntário do usuário autenticado")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Perfil de voluntário retornado com sucesso")
    @ApiResponse(responseCode = "404", description = "Perfil de voluntário não encontrado")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    public ResponseEntity<VolunteerDTOs.VolunteerResponse> mine(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(volunteerService.myVolunteer(principal.getUsername()));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar dados do voluntário", description = "Permite que o usuário atualize seus dados como voluntário")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Voluntário atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Voluntário não encontrado")
    @ApiResponse(responseCode = "403", description = "Acesso negado (apenas o proprietário pode atualizar)")
    public ResponseEntity<VolunteerDTOs.VolunteerResponse> update(
            @Parameter(description = "ID do voluntário") @PathVariable Long id,
            @RequestBody VolunteerDTOs.VolunteerRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(volunteerService.update(id, req, principal.getUsername()));
    }

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Aprovar voluntário", description = "Admin aprova a candidatura de um voluntário")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Voluntário aprovado com sucesso")
    @ApiResponse(responseCode = "404", description = "Voluntário não encontrado")
    @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)")
    public ResponseEntity<VolunteerDTOs.VolunteerResponse> approve(
            @Parameter(description = "ID do voluntário") @PathVariable Long id) {
        return ResponseEntity.ok(volunteerService.approve(id));
    }

    @PatchMapping("/{id}/reject")
    @Operation(summary = "Rejeitar voluntário", description = "Admin rejeita a candidatura de um voluntário")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Voluntário rejeitado com sucesso")
    @ApiResponse(responseCode = "404", description = "Voluntário não encontrado")
    @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)")
    public ResponseEntity<VolunteerDTOs.VolunteerResponse> reject(
            @Parameter(description = "ID do voluntário") @PathVariable Long id) {
        return ResponseEntity.ok(volunteerService.reject(id));
    }

    @GetMapping
    @Operation(summary = "Listar todos os voluntários", description = "Retorna todos os voluntários cadastrados (requer ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Lista de voluntários retornada com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)")
    public ResponseEntity<List<VolunteerDTOs.VolunteerResponse>> listAll() {
        return ResponseEntity.ok(volunteerService.listAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar voluntário", description = "Permite que um voluntário delete seu próprio perfil")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Voluntário deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Voluntário não encontrado")
    @ApiResponse(responseCode = "403", description = "Acesso negado (apenas o proprietário pode deletar)")
    public ResponseEntity<VolunteerDTOs.VolunteerResponse> delete(
            @Parameter(description = "ID do voluntário") @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(volunteerService.deleteById(id, principal.getUsername()));
    }

    @DeleteMapping("/type/{skillType}")
    @Operation(summary = "Deletar voluntários por tipo", description = "Admin deleta todos os voluntários com um tipo específico de habilidade")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Voluntários deletados com sucesso")
    @ApiResponse(responseCode = "404", description = "Nenhum voluntário encontrado com o tipo especificado")
    @ApiResponse(responseCode = "403", description = "Acesso negado (requer role ADMIN)")
    public ResponseEntity<Void> deleteByType(
            @Parameter(description = "Tipo de habilidade") @PathVariable String skillType) {
        volunteerService.deleteBySkillType(skillType);
        return ResponseEntity.noContent().build();
    }
}
