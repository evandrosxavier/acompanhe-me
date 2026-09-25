package br.com.acompanheme.gestaosolicitacoes.dto.solicitacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Procedimento solicitado")
public record ProcedimentoDTO(

        @Schema(description = "Código do procedimento (texto livre por ora)", example = "0301010072", nullable = true)
        @Size(max = 20, message = "Código do procedimento deve ter no máximo 20 caracteres")
        String codigoProcedimento,

        @Schema(description = "Descrição do procedimento", example = "Ecocardiograma transtorácico")
        @NotBlank(message = "Descrição do procedimento é obrigatória")
        @Size(max = 255, message = "Descrição do procedimento deve ter no máximo 255 caracteres")
        String descricaoProcedimento
) {}
