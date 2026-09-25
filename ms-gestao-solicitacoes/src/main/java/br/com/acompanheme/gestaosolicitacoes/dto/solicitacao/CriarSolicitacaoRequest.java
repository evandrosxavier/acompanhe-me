package br.com.acompanheme.gestaosolicitacoes.dto.solicitacao;

import br.com.acompanheme.gestaosolicitacoes.model.enums.Modalidade;
import br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade;
import br.com.acompanheme.gestaosolicitacoes.model.enums.TipoSolicitacao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Dados para criação de uma solicitação (consulta, exame ou cirurgia)")
public record CriarSolicitacaoRequest(

        @Schema(description = "ID da consulta de origem; opcional, pois vem do path. Se informado, deve coincidir com o path", example = "42", nullable = true)
        Long consultaId,

        @Schema(description = "Paciente")
        @NotNull(message = "Paciente é obrigatório")
        @Valid
        PacienteDTO paciente,

        @Schema(description = "Médico solicitante")
        @NotNull(message = "Médico é obrigatório")
        @Valid
        MedicoDTO medico,

        @Schema(description = "Unidade solicitante")
        @NotNull(message = "Unidade solicitante é obrigatória")
        @Valid
        UnidadeSolicitanteDTO unidadeSolicitante,

        @Schema(description = "Tipo da solicitação", example = "CIRURGIA")
        @NotNull(message = "Tipo da solicitação é obrigatório")
        TipoSolicitacao tipoSolicitacao,

        @Schema(description = "Procedimentos solicitados (ao menos um)")
        @NotEmpty(message = "Informe ao menos um procedimento")
        @Valid
        List<@NotNull(message = "Procedimento não pode ser nulo") ProcedimentoDTO> procedimentos,

        @Schema(description = "Modalidade (aplica-se apenas quando o tipo é CIRURGIA)", example = "AMBULATORIAL", nullable = true)
        Modalidade modalidade,

        @Schema(description = "Prioridade", example = "ALTA")
        @NotNull(message = "Prioridade é obrigatória")
        Prioridade prioridade,

        @Schema(description = "Motivo da urgência", nullable = true)
        String motivoDaUrgencia,

        @Schema(description = "Diagnóstico", example = "Estenose aórtica")
        @NotBlank(message = "Diagnóstico é obrigatório")
        String diagnostico,

        @Schema(description = "Observações", nullable = true)
        String observacoes
) {}
