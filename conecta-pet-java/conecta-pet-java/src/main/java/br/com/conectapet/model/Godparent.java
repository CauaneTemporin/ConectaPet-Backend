package br.com.conectapet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "godparents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Godparent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @NotNull
    @DecimalMin("1.00")
    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private GodparentStatus status = GodparentStatus.PENDENTE;

    @CreationTimestamp
    @Column(updatable = false, name = "started_at")
    private LocalDateTime startedAt;

    public enum GodparentStatus { PENDENTE, APROVADO, REJEITADO, INATIVO }
}
