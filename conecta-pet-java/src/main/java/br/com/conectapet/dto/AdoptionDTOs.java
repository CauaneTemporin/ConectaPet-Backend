package br.com.conectapet.dto;

import br.com.conectapet.model.Adoption;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AdoptionDTOs {

    public record AdoptionRequest(
        @NotNull(message = "ID do animal é obrigatório") Long animalId,
        String message
    ) {}

    public record ReviewRequest(
        @NotNull(message = "Status é obrigatório") Adoption.AdoptionStatus status
    ) {}

    public record AdoptionResponse(
        Long id, Long animalId, String animalName, String animalSpecies, String animalPhoto,
        Long userId, String userName, String userEmail,
        String status, String message, LocalDateTime createdAt, LocalDateTime reviewedAt
    ) {
        public static AdoptionResponse from(Adoption a) {
            return new AdoptionResponse(
                a.getId(),
                a.getAnimal().getId(), a.getAnimal().getName(),
                a.getAnimal().getSpecies() != null ? a.getAnimal().getSpecies().name().toLowerCase() : null,
                a.getAnimal().getPhotoUrl(),
                a.getUser().getId(), a.getUser().getName(), a.getUser().getEmail(),
                a.getStatus().name().toLowerCase(), a.getMessage(),
                a.getCreatedAt(), a.getReviewedAt()
            );
        }
    }
}
