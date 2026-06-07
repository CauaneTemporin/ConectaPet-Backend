package br.com.conectapet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "animals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Species species;

    private String breed;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Double ageYears;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Size size = Size.MEDIO;

    @Column(length = 1000)
    private String description;

    private String temperament;

    @Builder.Default
    private Boolean vaccinated = false;

    @Builder.Default
    private Boolean neutered = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AnimalStatus status = AnimalStatus.DISPONIVEL;

    private String photoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ong_id")
    private Ong ong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id")
    private Shelter shelter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Adoption> adoptions;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Godparent> godparents;

    // ── Enums ──
    public enum Species { CACHORRO, GATO, OUTRO }
    public enum Gender   { MACHO, FEMEA }
    public enum Size     { PEQUENO, MEDIO, GRANDE }
    public enum AnimalStatus { DISPONIVEL, ADOTADO, APADRINHADO }
}
