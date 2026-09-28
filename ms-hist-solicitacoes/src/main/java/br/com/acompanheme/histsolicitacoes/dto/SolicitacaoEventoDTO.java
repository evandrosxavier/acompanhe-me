package br.com.acompanheme.histsolicitacoes.dto;

import java.util.UUID;

/**
 * Cópia local do evento publicado pelo ms-gestao-solicitacoes no tópico solicitacao-eventos.
 * Os nomes dos campos precisam bater com o JSON do produtor; campos que não existem aqui são ignorados.
 */
public record SolicitacaoEventoDTO(
        UUID solicitacaoId,
        String tipoEvento,
        String statusAtual,
        String motivo,
        String prioridadeAnterior,
        String prioridadeNova,
        String autor,
        String unidadeExecucaoNome,
        String unidadeExecucaoMunicipio,
        String cpfPaciente,
        String nomePaciente,
        String dataEvento
) {}
