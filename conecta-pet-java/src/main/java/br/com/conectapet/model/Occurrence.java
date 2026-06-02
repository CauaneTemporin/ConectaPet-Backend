package br.com.conectapet.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "occurrences")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Occurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OccurrenceType type;

    private String titulo;

    @Column(nullable = false, length = 2000)
    private String description;

    private String estado;
    private String cidade;

    @Column(length = 9)
    private String cep;

    private String complemento;
    private String endereco;

    @Column(length = 1000)
    private String animalDescription;

    @Column(length = 500)
    private String animalIdentification;

    private String reporterName;
    private String reporterEmail;

    @Builder.Default
    private Boolean anonymous = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OccurrenceStatus status = OccurrenceStatus.PENDENTE;

    @Column(length = 1000)
    private String adminNotes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum OccurrenceType {
        @JsonProperty("abandono")         ABANDONO,
        @JsonProperty("maus_tratos")      MAUS_TRATOS,
        @JsonProperty("suspeita")         SUSPEITA,
        @JsonProperty("comercio_ilegal")  COMERCIO_ILEGAL,
        @JsonProperty("falta_saneamento") FALTA_SANEAMENTO,
        @JsonProperty("outro")            OUTRO
    }

    public enum OccurrenceStatus {
        @JsonProperty("pendente")   PENDENTE,
        @JsonProperty("em_analise") EM_ANALISE,
        @JsonProperty("resolvido")  RESOLVIDO,
        @JsonProperty("arquivado")  ARQUIVADO
    }
}
