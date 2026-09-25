package br.com.acompanheme.gestaosolicitacoes.dto.solicitacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Unidade de saúde que está solicitando o atendimento")
public record UnidadeSolicitanteDTO(

        @Schema(description = "Código da unidade", example = "2079798")
        @NotBlank(message = "Código da unidade é obrigatório")
        @Size(max = 20, message = "Código da unidade deve ter no máximo 20 caracteres")
        String codigo,

        @Schema(description = "Nome da unidade", example = "UBS Central")
        @NotBlank(message = "Nome da unidade é obrigatório")
        @Size(max = 150, message = "Nome da unidade deve ter no máximo 150 caracteres")
        String nome,

        @Schema(description = "Município da unidade", example = "Campinas")
        @NotBlank(message = "Município da unidade é obrigatório")
        @Size(max = 100, message = "Município da unidade deve ter no máximo 100 caracteres")
        String municipio,

        @Schema(description = "Bairro da unidade", example = "Cambuí")
        @NotBlank(message = "Bairro da unidade é obrigatório")
        @Size(max = 100, message = "Bairro da unidade deve ter no máximo 100 caracteres")
        String bairro
) {}
