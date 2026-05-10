package br.com.conectapet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "shelters")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String city;
    private String address;
    private String phone;
    private String email;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
