package br.com.acompanheme.gestaosolicitacoes.service;

import br.com.acompanheme.gestaosolicitacoes.dto.notificacao.SolicitacaoEvento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Publica no Kafka os eventos registrados pelo {@link SolicitacaoService}, somente após o commit
 * da transação que os originou.
 */
@Component
@RequiredArgsConstructor
public class SolicitacaoEventoListener {

    private final KafkaProducerService kafkaProducerService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aoConfirmarTransacao(SolicitacaoEvento evento) {
        kafkaProducerService.publicarEvento(evento);
    }
}
