package br.com.acompanheme.notificacoes.dto;
import java.util.UUID;
public record SolicitacaoNotificacaoDTO(
        UUID solicitacaoId,
        String tipoEvento,
        String statusAtual,
        String motivo,
        String nomePaciente,
        String emailPaciente,
        String dataEvento
) {}

