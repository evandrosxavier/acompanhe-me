package br.com.acompanheme.notificacoes.repository;

import br.com.acompanheme.notificacoes.model.NotificacaoLog;
import br.com.acompanheme.notificacoes.model.StatusEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacaoLogRepository extends JpaRepository<NotificacaoLog, Long> {

    List<NotificacaoLog> findBySolicitacaoId(UUID solicitacaoId);

    List<NotificacaoLog> findByStatus(StatusEnvio status);

    List<NotificacaoLog> findByDestinatario(String destinatario);
}

