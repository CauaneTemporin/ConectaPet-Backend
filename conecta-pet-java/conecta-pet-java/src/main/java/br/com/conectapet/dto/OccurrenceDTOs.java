package br.com.conectapet.dto;

import br.com.conectapet.model.Occurrence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class OccurrenceDTOs {

    public record OccurrenceRequest(
        @NotNull(message = "Tipo é obrigatório")
        Occurrence.OccurrenceType type,

        String titulo,

        @NotBlank(message = "Descrição é obrigatória")
        String description,

        String estado,
        String cidade,
        String cep,
        String complemento,
        String endereco,

        String animalDescription,
        String animalIdentification,

        String reporterName,
        String reporterEmail,
        Boolean anonymous
    ) {}

    public record UpdateStatusRequest(
        @NotNull(message = "Status é obrigatório")
        Occurrence.OccurrenceStatus status,
        String adminNotes
    ) {}

    public record OccurrenceResponse(
        Long id,
        Occurrence.OccurrenceType type,
        String titulo,
        String description,
        String estado,
        String cidade,
        String cep,
        String complemento,
        String endereco,
        String animalDescription,
        String animalIdentification,
        String reporterName,
        String reporterEmail,
        Boolean anonymous,
        Long userId,
        Occurrence.OccurrenceStatus status,
        String adminNotes,
        String analisadoPorNome,
        LocalDateTime analisadoEm,
        LocalDateTime createdAt
    ) {
        public static OccurrenceResponse from(Occurrence o) {
            return from(o, false);
        }

        public static OccurrenceResponse from(Occurrence o, boolean maskReporterInfo) {
            boolean hide = maskReporterInfo && Boolean.TRUE.equals(o.getAnonymous());
            return new OccurrenceResponse(
                o.getId(), o.getType(), o.getTitulo(), o.getDescription(),
                o.getEstado(), o.getCidade(), o.getCep(), o.getComplemento(), o.getEndereco(),
                o.getAnimalDescription(), o.getAnimalIdentification(),
                hide ? null : o.getReporterName(),
                hide ? null : o.getReporterEmail(),
                o.getAnonymous(),
                hide ? null : (o.getUser() != null ? o.getUser().getId() : null),
                o.getStatus(), o.getAdminNotes(),
                o.getAnalisadoPor() != null ? o.getAnalisadoPor().getName() : null,
                o.getAnalisadoEm(),
                o.getCreatedAt()
            );
        }
    }
}
