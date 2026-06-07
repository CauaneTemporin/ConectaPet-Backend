package br.com.conectapet.dto;

import br.com.conectapet.model.User;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminDTOs {

    public record AdminStatsResponse(
        long totalAnimals, long availableAnimals, long adoptedAnimals,
        long totalUsers, long totalAdoptions, long pendingAdoptions,
        BigDecimal totalDonations, long totalVolunteers, long totalGodparents, long unreadContacts,
        long totalOngs, long pendingOngs, long activeOngs
    ) {}

    public record AlterarRoleRequest(
        @NotNull(message = "Role é obrigatório") User.Role role
    ) {}

    public record UserAdminResponse(
        Long id,
        String name,
        String email,
        String city,
        User.Role role,
        LocalDateTime createdAt
    ) {}
}
