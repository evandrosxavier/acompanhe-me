package br.com.acompanheme.gestaosolicitacoes.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Corpo de erro simples")
public record ErroResponse(

        @Schema(description = "Mensagem descritiva do erro")
        String mensagem,

        @Schema(description = "Momento em que o erro ocorreu")
        LocalDateTime timestamp
) {}
