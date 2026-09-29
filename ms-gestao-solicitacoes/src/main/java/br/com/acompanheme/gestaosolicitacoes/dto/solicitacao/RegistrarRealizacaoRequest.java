package br.com.acompanheme.gestaosolicitacoes.dto.solicitacao;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Dados do registro de realização do atendimento")
public record RegistrarRealizacaoRequest(

        @Schema(description = "Momento em que o atendimento aconteceu (yyyy-MM-dd'T'HH:mm:ss); se omitido, usa o momento do registro. Não pode ser futuro",
                example = "2026-09-28T14:30:00", nullable = true)
        LocalDateTime dataAtendimento
) {}
