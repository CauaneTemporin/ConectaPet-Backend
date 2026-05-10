package br.com.conectapet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "donations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "1.00", message = "Valor mínimo é R$ 1,00")
    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Frequency frequency = Frequency.UNICO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentMethod method = PaymentMethod.PIX;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DonationStatus status = DonationStatus.PENDENTE;

    private String donorName;
    private String donorEmail;
    private String donorCpf;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum Frequency      { UNICO, MENSAL }
    public enum PaymentMethod  { PIX, CARTAO, BOLETO }
    public enum DonationStatus { PENDENTE, CONFIRMADO, CANCELADO }
}
