package br.com.acompanheme.gestaosolicitacoes.dto.solicitacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados do médico solicitante")
public record MedicoDTO(

        @Schema(description = "Nome do médico", example = "Dr. João Pereira")
        @NotBlank(message = "Nome do médico é obrigatório")
        @Size(max = 150, message = "Nome do médico deve ter no máximo 150 caracteres")
        String nome,

        @Schema(description = "CRM do médico", example = "123456-SP")
        @NotBlank(message = "CRM é obrigatório")
        @Size(max = 20, message = "CRM deve ter no máximo 20 caracteres")
        String crm,

        @Schema(description = "Especialidade do médico", example = "Cardiologia")
        @NotBlank(message = "Especialidade é obrigatória")
        @Size(max = 100, message = "Especialidade deve ter no máximo 100 caracteres")
        String especialidade
) {}
