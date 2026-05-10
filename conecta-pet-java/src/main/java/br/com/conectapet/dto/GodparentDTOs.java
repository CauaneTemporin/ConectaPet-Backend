package br.com.conectapet.dto;

import br.com.conectapet.model.Godparent;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class GodparentDTOs {

    public record GodparentRequest(
        @NotNull Long animalId,
        @NotNull @DecimalMin("1.00") Double amount
    ) {}

    public record GodparentResponse(
        Long id, Long animalId, String animalName, String animalSpecies,
        String animalPhoto, Double amount, String status, LocalDateTime startedAt
    ) {
        public static GodparentResponse from(Godparent g) {
            return new GodparentResponse(
                g.getId(), g.getAnimal().getId(), g.getAnimal().getName(),
                g.getAnimal().getSpecies() != null ? g.getAnimal().getSpecies().name().toLowerCase() : null,
                g.getAnimal().getPhotoUrl(), g.getAmount(),
                g.getStatus().name().toLowerCase(), g.getStartedAt()
            );
        }
    }
}
