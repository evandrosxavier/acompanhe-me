package br.com.acompanheme.histsolicitacoes.dto;

import br.com.acompanheme.histsolicitacoes.model.HistoricoSolicitacao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record HistoricoSolicitacaoDTO(
        UUID id,
        UUID solicitacaoId,
        String tipoEvento,
        String statusAtual,
        String prioridadeAnterior,
        String prioridadeNova,
        String autor,
        String motivo,
        String unidadeExecucaoNome,
        String unidadeExecucaoMunicipio,
        String pacienteCpf,
        String pacienteNome,
        String dataEvento,
        String dataRegistro
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static HistoricoSolicitacaoDTO fromEntity(HistoricoSolicitacao h) {
        return new HistoricoSolicitacaoDTO(
                h.getId(),
                h.getSolicitacaoId(),
                nome(h.getTipoEvento()),
                nome(h.getStatusAtual()),
                nome(h.getPrioridadeAnterior()),
                nome(h.getPrioridadeNova()),
                h.getAutor(),
                h.getMotivo(),
                h.getUnidadeExecucaoNome(),
                h.getUnidadeExecucaoMunicipio(),
                h.getPacienteCpf(),
                h.getPacienteNome(),
                formatar(h.getDataEvento()),
                formatar(h.getDataRegistro()));
    }

    private static String nome(Enum<?> valor) {
        return valor != null ? valor.name() : null;
    }

    private static String formatar(LocalDateTime data) {
        return data != null ? data.format(FORMATTER) : null;
    }
}
