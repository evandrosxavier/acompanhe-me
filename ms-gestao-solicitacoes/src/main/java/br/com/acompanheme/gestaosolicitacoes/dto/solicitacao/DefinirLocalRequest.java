package br.com.acompanheme.gestaosolicitacoes.dto.solicitacao;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Unidade de execução definida para a solicitação")
public record DefinirLocalRequest(

        @Schema(description = "Código da unidade", example = "2079798")
        String codigo,

        @Schema(description = "Nome da unidade", example = "Hospital Municipal")
        String nome,

        @Schema(description = "Município da unidade", example = "Campinas")
        String municipio,

        @Schema(description = "Bairro da unidade", example = "Centro")
        String bairro,

        @Schema(description = "Endereço da unidade", example = "Rua das Flores, 100")
        String endereco,

        @Schema(description = "DDD do telefone", example = "19")
        String ddd,

        @Schema(description = "Telefone da unidade", example = "32001000")
        String telefone
) {}
