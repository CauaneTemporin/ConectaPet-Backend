package br.com.conectapet.dto;

import br.com.conectapet.model.Shelter;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ShelterDTOs {

    public record ShelterRequest(
        @NotBlank String name, String city, String address, String phone, String email
    ) {}

    public record ShelterResponse(
        Long id, String name, String city, String address, String phone, String email, LocalDateTime createdAt
    ) {
        public static ShelterResponse from(Shelter s) {
            return new ShelterResponse(s.getId(), s.getName(), s.getCity(),
                s.getAddress(), s.getPhone(), s.getEmail(), s.getCreatedAt());
        }
    }
}
