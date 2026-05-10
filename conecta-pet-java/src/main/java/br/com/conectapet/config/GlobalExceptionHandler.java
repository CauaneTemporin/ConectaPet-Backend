package br.com.conectapet.config;

import br.com.conectapet.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** Erros de validação (@Valid) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse.ValidationError> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach(err -> {
            String field = err instanceof FieldError fe ? fe.getField() : err.getObjectName();
            fields.put(field, err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(new ErrorResponse.ValidationError(fields));
    }

    /** Erros de negócio lançados via ResponseStatusException */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse.ApiError> handleResponseStatus(
            ResponseStatusException ex) {
        var body = new ErrorResponse.ApiError(
            ex.getStatusCode().value(),
            ex.getStatusCode().toString(),
            ex.getReason() != null ? ex.getReason() : ex.getMessage()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    /** Acesso negado (403) */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse.ApiError> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse.ApiError(403, "Acesso negado",
                "Você não tem permissão para realizar esta ação."));
    }

    /** Não autenticado (401) */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse.ApiError> handleAuth(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse.ApiError(401, "Não autenticado",
                "Faça login para continuar."));
    }

    /** Erros genéricos (500) */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse.ApiError> handleGeneral(Exception ex) {
        log.error("Erro interno: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse.ApiError(500, "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente."));
    }
}
