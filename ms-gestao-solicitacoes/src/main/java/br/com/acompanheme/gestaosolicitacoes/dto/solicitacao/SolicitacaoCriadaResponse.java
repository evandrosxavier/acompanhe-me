package br.com.acompanheme.gestaosolicitacoes.dto.solicitacao;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Identificador da solicitação criada")
public record SolicitacaoCriadaResponse(

        @Schema(description = "ID da solicitação criada")
        UUID id
) {}
