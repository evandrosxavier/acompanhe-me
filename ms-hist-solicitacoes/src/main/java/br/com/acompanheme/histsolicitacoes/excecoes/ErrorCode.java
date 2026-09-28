package br.com.acompanheme.histsolicitacoes.excecoes;

public enum ErrorCode {

    PAGINACAO_INVALIDA("Paginação inválida: page deve ser >= 0 e size entre 1 e 100."),
    CPF_INVALIDO("CPF inválido: informe 11 dígitos, com ou sem máscara.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
