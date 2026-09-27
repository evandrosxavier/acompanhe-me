package br.com.acompanheme.gestaosolicitacoes.excecoes;

import br.com.acompanheme.gestaosolicitacoes.model.enums.Status;

public class SolicitacaoSemPosicaoException extends RuntimeException {

    public SolicitacaoSemPosicaoException(Status statusAtual) {
        super("A solicitação não está em fila de espera no momento (status atual: %s)."
                .formatted(statusAtual));
    }
}
