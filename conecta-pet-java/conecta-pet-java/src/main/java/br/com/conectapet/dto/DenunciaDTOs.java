package br.com.conectapet.dto;

import br.com.conectapet.model.Denuncia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class DenunciaDTOs {

    public record CriarDenunciaRequest(
        @NotBlank(message = "Título é obrigatório") String titulo,
        @NotBlank(message = "Descrição é obrigatória") String descricao,
        String estado,
        String cidade,
        String cep,
        String complemento,
        String endereco,
        @NotNull(message = "Categoria é obrigatória") Denuncia.Categoria categoria
    ) {}

    public record AtualizarStatusRequest(
        @NotNull(message = "Status é obrigatório") Denuncia.StatusDenuncia status,
        String observacaoGestor
    ) {}

    public record DenunciaResponse(
        Long id,
        Long userId,
        String userName,
        String titulo,
        String descricao,
        String estado,
        String cidade,
        String cep,
        String complemento,
        String endereco,
        Denuncia.Categoria categoria,
        Denuncia.StatusDenuncia status,
        String observacaoGestor,
        String analisadoPorNome,
        LocalDateTime analisadoEm,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}
}
