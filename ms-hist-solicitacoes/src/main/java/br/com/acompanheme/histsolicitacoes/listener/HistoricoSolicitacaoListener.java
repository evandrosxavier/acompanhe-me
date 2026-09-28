package br.com.acompanheme.histsolicitacoes.listener;

import br.com.acompanheme.histsolicitacoes.dto.SolicitacaoEventoDTO;
import br.com.acompanheme.histsolicitacoes.service.HistoricoSolicitacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoricoSolicitacaoListener {

    private final HistoricoSolicitacaoService historicoService;

    @KafkaListener(topics = "solicitacao-eventos", groupId = "${spring.kafka.consumer.group-id}",
            autoStartup = "${hist.kafka.listener.auto-startup:true}")
    public void consumirEventoSolicitacao(SolicitacaoEventoDTO evento) {
        log.info("[{}] Evento recebido | Solicitacao ID: {} | Status: {}",
                evento.tipoEvento(), evento.solicitacaoId(), evento.statusAtual());
        historicoService.registrar(evento);
    }
}
