package br.com.acompanheme.gestaosolicitacoes.excecoes;

import br.com.acompanheme.gestaosolicitacoes.model.enums.Status;

public class TransicaoInvalidaException extends RuntimeException {

    public TransicaoInvalidaException(String message) {
        super(message);
    }

    public static TransicaoInvalidaException para(Status statusAtual, String acao) {
        return new TransicaoInvalidaException(
                "Não é possível executar '%s' a partir do status atual: %s".formatted(acao, statusAtual));
    }
}
