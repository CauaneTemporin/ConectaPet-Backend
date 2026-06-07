package br.com.conectapet.dto;

import br.com.conectapet.model.Volunteer;
import java.time.LocalDateTime;

public class VolunteerDTOs {

    public record VolunteerRequest(String skills, String availability, String motivation) {}

    public record VolunteerResponse(
        Long id, Long userId, String userName, String userEmail, String userPhone,
        String skills, String availability, String motivation, String status, LocalDateTime createdAt
    ) {
        public static VolunteerResponse from(Volunteer v) {
            return new VolunteerResponse(
                v.getId(), v.getUser().getId(), v.getUser().getName(),
                v.getUser().getEmail(), v.getUser().getPhone(), v.getSkills(), v.getAvailability(),
                v.getMotivation(), v.getStatus().name(), v.getCreatedAt()
            );
        }
    }
}
