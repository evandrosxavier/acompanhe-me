package br.com.acompanheme.gestaosolicitacoes.dto.solicitacao;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Posição da solicitação na fila de espera")
public record PosicaoFilaResponse(

        @Schema(description = "ID da solicitação")
        UUID solicitacaoId,

        @Schema(description = "Posição na fila (1 = próxima a ser atendida)", example = "3")
        long posicao,

        @Schema(description = "Quantidade total de solicitações na mesma fila (tipo e modalidade)", example = "12")
        long tamanhoTotalFila
) {}
