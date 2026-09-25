package br.com.acompanheme.gestaosolicitacoes.dto.solicitacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Endereço resumido do paciente (apenas o necessário para a regulação comparar proximidade)")
public record EnderecoDTO(

        @Schema(description = "Município de residência", example = "Campinas")
        @NotBlank(message = "Município é obrigatório")
        @Size(max = 100, message = "Município deve ter no máximo 100 caracteres")
        String municipio,

        @Schema(description = "Bairro de residência", example = "Cambuí")
        @NotBlank(message = "Bairro é obrigatório")
        @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
        String bairro
) {}
