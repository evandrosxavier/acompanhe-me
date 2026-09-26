package br.com.acompanheme.notificacoes.dto;

import br.com.acompanheme.notificacoes.model.NotificacaoLog;
import br.com.acompanheme.notificacoes.model.StatusEnvio;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificacaoLogResponseDTO(
        Long id,
        UUID solicitacaoId,
        String tipoEvento,
        String statusConsulta,
        String destinatario,
        String pacienteNome,
        String assunto,
        StatusEnvio status,
        String mensagemErro,
        LocalDateTime dataEnvio
) {
    public static NotificacaoLogResponseDTO fromEntity(NotificacaoLog entity) {
        return new NotificacaoLogResponseDTO(
                entity.getId(),
                entity.getSolicitacaoId(),
                entity.getTipoEvento(),
                entity.getStatusSolicitacao(),
                entity.getDestinatario(),
                entity.getPacienteNome(),
                entity.getAssunto(),
                entity.getStatus(),
                entity.getMensagemErro(),
                entity.getDataEnvio()
        );
    }
}

