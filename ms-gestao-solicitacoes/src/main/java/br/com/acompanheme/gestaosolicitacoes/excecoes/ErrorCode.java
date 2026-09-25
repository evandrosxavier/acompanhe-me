package br.com.acompanheme.gestaosolicitacoes.excecoes;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SOLICITACAO_NAO_ENCONTRADA("Solicitação não encontrada com o identificador informado."),
    CONSULTA_ID_DIVERGENTE("O consultaId informado no corpo difere do consultaId do caminho da requisição.");

    private final String message;
}
