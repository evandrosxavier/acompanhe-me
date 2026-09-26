package br.com.acompanheme.notificacoes.service;

import br.com.acompanheme.notificacoes.dto.NotificacaoLogResponseDTO;
import br.com.acompanheme.notificacoes.model.NotificacaoLog;
import br.com.acompanheme.notificacoes.model.StatusEnvio;
import br.com.acompanheme.notificacoes.repository.NotificacaoLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacaoLogService {

    private final NotificacaoLogRepository repository;

    public void registrarSucesso(UUID solicitacaoId, String tipoEvento, String statusSolicitacao,
                                  String destinatario, String pacienteNome, String assunto) {
        salvar(solicitacaoId, tipoEvento, statusSolicitacao, destinatario, pacienteNome, assunto, StatusEnvio.ENVIADO, null);
    }

    public void registrarFalha(UUID solicitacaoId, String tipoEvento, String statusSolicitacao,
                                String destinatario, String pacienteNome, String assunto,
                                String mensagemErro) {
        salvar(solicitacaoId, tipoEvento, statusSolicitacao, destinatario, pacienteNome, assunto, StatusEnvio.FALHA, mensagemErro);
    }

    public List<NotificacaoLogResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(NotificacaoLogResponseDTO::fromEntity)
                .toList();
    }

    public List<NotificacaoLogResponseDTO> buscarPorSolicitacaoId(UUID solicitacaoId) {
        return repository.findBySolicitacaoId(solicitacaoId).stream()
                .map(NotificacaoLogResponseDTO::fromEntity)
                .toList();
    }

    public List<NotificacaoLogResponseDTO> buscarPorStatus(StatusEnvio status) {
        return repository.findByStatus(status).stream()
                .map(NotificacaoLogResponseDTO::fromEntity)
                .toList();
    }

    public List<NotificacaoLogResponseDTO> buscarPorDestinatario(String destinatario) {
        return repository.findByDestinatario(destinatario).stream()
                .map(NotificacaoLogResponseDTO::fromEntity)
                .toList();
    }

    private void salvar(UUID solicitacaoId, String tipoEvento, String statusSolicitacao, String destinatario,
                        String pacienteNome, String assunto, StatusEnvio status, String mensagemErro) {
        try {
            NotificacaoLog notificacaoLog = NotificacaoLog.builder()
                    .solicitacaoId(solicitacaoId)
                    .tipoEvento(tipoEvento)
                    .statusSolicitacao(statusSolicitacao)
                    .destinatario(destinatario)
                    .pacienteNome(pacienteNome)
                    .assunto(assunto)
                    .status(status)
                    .mensagemErro(mensagemErro)
                    .build();
            repository.save(notificacaoLog);
        } catch (Exception ex) {
            log.error("Erro ao persistir log de notificacao: {}", ex.getMessage());
        }
    }
}

