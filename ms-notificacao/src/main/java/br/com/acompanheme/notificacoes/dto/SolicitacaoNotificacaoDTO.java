package br.com.acompanheme.notificacoes.dto;
import java.util.UUID;
public record SolicitacaoNotificacaoDTO(
        UUID solicitacaoId,
        String tipoEvento,
        String statusAtual,
        String motivo,
        String prioridadeAnterior,
        String prioridadeNova,
        String autor,
        String unidadeExecucaoNome,
        String unidadeExecucaoMunicipio,
        String nomePaciente,
        String emailPaciente,
        String dataEvento
) {}

