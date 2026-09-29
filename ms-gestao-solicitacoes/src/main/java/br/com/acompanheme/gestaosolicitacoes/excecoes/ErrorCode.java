package br.com.acompanheme.gestaosolicitacoes.excecoes;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SOLICITACAO_NAO_ENCONTRADA("Solicitação não encontrada com o identificador informado."),
    CONSULTA_ID_DIVERGENTE("O consultaId informado no corpo difere do consultaId do caminho da requisição."),
    MODALIDADE_OBRIGATORIA("A modalidade é obrigatória quando o tipo da solicitação é CIRURGIA."),
    MODALIDADE_NAO_PERMITIDA("A modalidade se aplica apenas a solicitações do tipo CIRURGIA."),
    MOTIVO_URGENCIA_OBRIGATORIO("O motivo da urgência é obrigatório quando a prioridade é URGENTE.");

    private final String message;
}
