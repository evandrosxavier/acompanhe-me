package br.com.acompanheme.gestaosolicitacoes.dto.solicitacao;

import br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Alteração de prioridade de uma solicitação em fila de espera")
public record AlterarPrioridadeRequest(

        @Schema(description = "Nova prioridade", example = "URGENTE")
        @NotNull(message = "Nova prioridade é obrigatória")
        Prioridade novaPrioridade,

        @Schema(description = "Justificativa da alteração")
        String justificativa
) {}
