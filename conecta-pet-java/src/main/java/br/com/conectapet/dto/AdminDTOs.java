package br.com.conectapet.dto;

import java.math.BigDecimal;

public class AdminDTOs {

    public record AdminStatsResponse(
        long totalAnimals, long availableAnimals, long adoptedAnimals,
        long totalUsers, long totalAdoptions, long pendingAdoptions,
        BigDecimal totalDonations, long totalVolunteers, long totalGodparents, long unreadContacts
    ) {}
}
