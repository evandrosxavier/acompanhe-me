package br.com.acompanheme.gestaosolicitacoes.dto.solicitacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Decisão da regulação sobre uma solicitação em análise")
public record AvaliarSolicitacaoRequest(

        @Schema(description = "Decisão da regulação", example = "APROVADA")
        @NotNull(message = "Decisão é obrigatória")
        Decisao decisao,

        @Schema(description = "Motivo da decisão; obrigatório quando decisao for NEGADA ou PENDENTE", nullable = true)
        String motivo
) {
    public enum Decisao {
        APROVADA,
        NEGADA,
        PENDENTE
    }
}
