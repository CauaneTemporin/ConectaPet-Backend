package br.com.conectapet.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {

    public record ApiError(int status, String error, String message, LocalDateTime timestamp) {
        public ApiError(int status, String error, String message) {
            this(status, error, message, LocalDateTime.now());
        }
    }

    public record ValidationError(int status, String error, Map<String, String> fields, LocalDateTime timestamp) {
        public ValidationError(Map<String, String> fields) {
            this(400, "Erro de validação", fields, LocalDateTime.now());
        }
    }
}
