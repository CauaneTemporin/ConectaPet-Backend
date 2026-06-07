package br.com.conectapet.dto;

import br.com.conectapet.model.Contact;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ContactDTOs {

    public record ContactRequest(
        @NotBlank String name, @Email @NotBlank String email,
        String phone, String subject, @NotBlank String message
    ) {}

    public record ContactResponse(
        Long id, String name, String email, String phone, String subject,
        String message, Boolean read, LocalDateTime createdAt
    ) {
        public static ContactResponse from(Contact c) {
            return new ContactResponse(c.getId(), c.getName(), c.getEmail(),
                c.getPhone(), c.getSubject(), c.getMessage(), c.getRead(), c.getCreatedAt());
        }
    }
}
