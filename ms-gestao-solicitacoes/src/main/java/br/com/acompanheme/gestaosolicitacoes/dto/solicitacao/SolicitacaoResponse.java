package br.com.acompanheme.gestaosolicitacoes.dto.solicitacao;

import br.com.acompanheme.gestaosolicitacoes.model.enums.Modalidade;
import br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade;
import br.com.acompanheme.gestaosolicitacoes.model.enums.Status;
import br.com.acompanheme.gestaosolicitacoes.model.enums.TipoSolicitacao;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Dados de uma solicitação")
public record SolicitacaoResponse(

        @Schema(description = "ID da solicitação")
        UUID id,

        @Schema(description = "ID da consulta de origem no ms-gestao-consultas", example = "42")
        Long consultaId,

        PacienteDTO paciente,

        MedicoDTO medico,

        UnidadeSolicitanteDTO unidadeSolicitante,

        @Schema(example = "CIRURGIA")
        TipoSolicitacao tipoSolicitacao,

        List<ProcedimentoDTO> procedimentos,

        @Schema(description = "Modalidade (apenas para CIRURGIA)", nullable = true)
        Modalidade modalidade,

        @Schema(example = "ALTA")
        Prioridade prioridade,

        @Schema(nullable = true)
        String motivoDaUrgencia,

        String diagnostico,

        @Schema(nullable = true)
        String observacoes,

        @Schema(description = "Motivo de pendência ou negativa", nullable = true)
        String parecerDaRegulacao,

        @Schema(example = "REGISTRADA")
        Status status,

        @Schema(description = "Unidade de execução; nula até ser definida", nullable = true)
        UnidadeExecucaoDTO unidadeExecucao,

        LocalDateTime dataSolicitacao,

        LocalDateTime dataAtualizacao,

        @Schema(description = "Data em que o pedido entrou na fila; nula até ser definida", nullable = true)
        LocalDateTime dataEntradaFila,


        @Schema(description = "Data do atendimento; nula até ser definida", nullable = true)
        LocalDateTime dataAtendimento
) {}
