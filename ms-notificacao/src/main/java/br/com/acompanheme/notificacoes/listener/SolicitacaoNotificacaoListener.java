package br.com.acompanheme.notificacoes.listener;

import br.com.acompanheme.notificacoes.dto.SolicitacaoNotificacaoDTO;
import br.com.acompanheme.notificacoes.service.NotificacaoEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitacaoNotificacaoListener {

    private final NotificacaoEmailService emailService;

    @KafkaListener(topics = "solicitacao-eventos", groupId = "${spring.kafka.consumer.group-id}")
    public void consumirEventoSolicitacao(SolicitacaoNotificacaoDTO mensagem) {
        log.info("[{}] Solicitacao ID: {} | Paciente: {} | Status: {} |",
                mensagem.tipoEvento(), mensagem.solicitacaoId(), mensagem.nomePaciente(),
                mensagem.statusAtual());
        emailService.notificar(mensagem);
    }
}

