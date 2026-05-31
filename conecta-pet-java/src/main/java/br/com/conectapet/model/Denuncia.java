package br.com.conectapet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "denuncias")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ong_id")
    private Ong ong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Título é obrigatório")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "Descrição é obrigatória")
    @Column(nullable = false, length = 2000)
    private String descricao;

    @Column
    private String endereco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusDenuncia status = StatusDenuncia.PENDENTE;

    @Column(length = 1000)
    private String observacaoGestor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analisado_por")
    private User analisadoPor;

    private LocalDateTime analisadoEm;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum Categoria {
        MAUS_TRATOS,
        ABANDONO,
        COMERCIO_ILEGAL,
        FALTA_DE_SANEAMENTO,
        OUTROS
    }

    public enum StatusDenuncia {
        PENDENTE,
        EM_ANALISE,
        RESOLVIDA,
        ARQUIVADA
    }
}
