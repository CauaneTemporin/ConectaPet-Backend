package br.com.conectapet.dto;

import br.com.conectapet.model.User;
import java.time.LocalDateTime;

public class UserDTOs {

    public record UserResponse(
        Long id, String name, String email, String city,
        String role, String bio, String avatarUrl, LocalDateTime createdAt
    ) {
        public static UserResponse from(User u) {
            return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getCity(),
                u.getRole().name(), u.getBio(), u.getAvatarUrl(), u.getCreatedAt());
        }
    }
}
