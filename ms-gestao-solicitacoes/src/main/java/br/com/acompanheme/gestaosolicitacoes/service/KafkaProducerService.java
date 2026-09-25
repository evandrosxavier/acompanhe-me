package br.com.acompanheme.gestaosolicitacoes.service;

import br.com.acompanheme.gestaosolicitacoes.dto.notificacao.SolicitacaoEvento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPICO = "solicitacao-eventos";

    public void publicarEvento(SolicitacaoEvento evento) {
        try {
            kafkaTemplate.send(TOPICO, evento.solicitacaoId().toString(), evento)
                         .whenComplete((result, ex) -> {
                             if (ex == null) {
                                 log.info("Evento {} da solicitação {} publicado na partição {}",
                                                    evento.tipoEvento(),
                                                    evento.solicitacaoId(),
                                                    result.getRecordMetadata().partition());
                             } else {
                                 log.error("Falha ao enviar mensagem para o tópico {}: {}", TOPICO, ex.getMessage());
                             }
                         });
        } catch (Exception e) {
            log.error("Falha ao enviar mensagem para o tópico {}: {}", TOPICO, e.getMessage());
        }
    }
}