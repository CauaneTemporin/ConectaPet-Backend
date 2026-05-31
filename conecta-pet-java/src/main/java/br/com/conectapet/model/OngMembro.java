package br.com.conectapet.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "ong_membros",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ong_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OngMembro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ong_id", nullable = false)
    private Ong ong;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OngMembroRole role = OngMembroRole.ONG_MEMBRO;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime joinedAt;

    public enum OngMembroRole { ONG_ADMIN, ONG_MEMBRO }
}
