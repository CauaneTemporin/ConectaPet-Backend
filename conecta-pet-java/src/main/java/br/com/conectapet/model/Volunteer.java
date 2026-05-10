package br.com.conectapet.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "volunteers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // JSON array stored as text: ["fotografia","transporte",...]
    @Column(length = 500)
    private String skills;

    private String availability;

    @Column(length = 1000)
    private String motivation;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VolunteerStatus status = VolunteerStatus.PENDENTE;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum VolunteerStatus { PENDENTE, APROVADO, REJEITADO }
}
