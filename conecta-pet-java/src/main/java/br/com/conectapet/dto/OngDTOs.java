package br.com.conectapet.dto;

import br.com.conectapet.model.Ong;
import br.com.conectapet.model.OngMembro;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class OngDTOs {

    public record CriarOngRequest(
        @NotBlank(message = "CNPJ é obrigatório")
        @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", message = "CNPJ inválido (formato: XX.XXX.XXX/XXXX-XX)")
        String cnpj,

        @NotBlank(message = "Razão social é obrigatória")
        String razaoSocial,

        @NotBlank(message = "Nome fantasia é obrigatório")
        String nomeFantasia,

        @Email(message = "E-mail inválido")
        @NotBlank(message = "E-mail é obrigatório")
        String email,

        String telefone,
        String endereco,

        @NotBlank(message = "Cidade é obrigatória")
        String cidade,

        @NotBlank(message = "Estado é obrigatório")
        @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres (ex: SP)")
        String estado,

        String descricao
    ) {}

    public record AtualizarStatusOngRequest(
        @NotNull(message = "Status é obrigatório")
        Ong.OngStatus status
    ) {}

    public record OngResumoResponse(
        Long id,
        String nomeFantasia,
        String razaoSocial,
        String cidade,
        String estado,
        String descricao,
        String logoUrl,
        Ong.OngStatus status,
        OngMembro.OngMembroRole myRole
    ) {}

    public record OngResponse(
        Long id,
        String cnpj,
        String razaoSocial,
        String nomeFantasia,
        String email,
        String telefone,
        String endereco,
        String cidade,
        String estado,
        String descricao,
        String logoUrl,
        Ong.OngStatus status,
        Long solicitadoPorId,
        String solicitadoPorNome,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}

    public record OngMembroResponse(
        Long id,
        Long userId,
        String userName,
        String userEmail,
        OngMembro.OngMembroRole role,
        LocalDateTime joinedAt
    ) {}

    public record AlterarMembroRoleRequest(
        @NotNull(message = "Role é obrigatório")
        OngMembro.OngMembroRole role
    ) {}
}
