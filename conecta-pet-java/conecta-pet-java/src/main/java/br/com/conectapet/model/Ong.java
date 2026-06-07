package br.com.conectapet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ongs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "CNPJ é obrigatório")
    @Column(nullable = false, unique = true, length = 18)
    private String cnpj;

    @NotBlank(message = "Razão social é obrigatória")
    @Column(nullable = false)
    private String razaoSocial;

    @NotBlank(message = "Nome fantasia é obrigatório")
    @Column(nullable = false)
    private String nomeFantasia;

    @Email
    @NotBlank(message = "E-mail é obrigatório")
    @Column(nullable = false)
    private String email;

    private String telefone;

    @Column(length = 9)
    private String cep;

    private String endereco;

    @NotBlank(message = "Cidade é obrigatória")
    @Column(nullable = false)
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Column(nullable = false, length = 2)
    private String estado;

    @Column(length = 1000)
    private String descricao;

    @Column(length = 3000)
    private String historia;

    @Column(length = 500)
    private String missao;

    private String logoUrl;

    @Column(columnDefinition = "TEXT")
    private String pixQrCodeUrl;

    @Column(columnDefinition = "TEXT")
    private String pixCopiaCola;

    private String facebook;
    private String whatsapp;
    private String instagram;
    private String youtube;
    private String tiktok;
    private String telegram;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OngStatus status = OngStatus.PENDENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitado_por")
    private User solicitadoPor;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "ong", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OngMembro> membros;

    public enum OngStatus { PENDENTE, ATIVA, INATIVA }
}
