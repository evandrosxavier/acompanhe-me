package br.com.acompanheme.gestaosolicitacoes.model.enums;

public enum Prioridade {
    BAIXA,
    MEDIA,
    ALTA,
    URGENTE;

    /**
     * Posição da prioridade na ordenação da fila (menor = atendida antes).
     * Deve seguir a mesma ordem do CASE usado nas queries do SolicitacaoRepository.
     */
    public int rank() {
        return switch (this) {
            case URGENTE -> 0;
            case ALTA -> 1;
            case MEDIA -> 2;
            case BAIXA -> 3;
        };
    }
}
