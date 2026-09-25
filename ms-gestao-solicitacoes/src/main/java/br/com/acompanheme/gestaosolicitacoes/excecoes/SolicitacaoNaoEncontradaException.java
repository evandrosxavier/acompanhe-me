package br.com.acompanheme.gestaosolicitacoes.excecoes;

import org.springframework.http.HttpStatus;

public class SolicitacaoNaoEncontradaException extends BusinessException {

    public SolicitacaoNaoEncontradaException() {
        super(ErrorCode.SOLICITACAO_NAO_ENCONTRADA, HttpStatus.NOT_FOUND);
    }
}
