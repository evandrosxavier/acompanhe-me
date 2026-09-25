package br.com.acompanheme.gestaosolicitacoes.dto.solicitacao;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta do paciente à oferta de local de atendimento")
public record ResponderOfertaRequest(

        @Schema(description = "true se o paciente aceitou o local ofertado; false se recusou", example = "true")
        boolean aceita
) {}
