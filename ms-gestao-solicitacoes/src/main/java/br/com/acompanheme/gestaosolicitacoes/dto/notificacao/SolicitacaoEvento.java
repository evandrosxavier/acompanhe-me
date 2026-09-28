package br.com.acompanheme.gestaosolicitacoes.dto.notificacao;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade;
import br.com.acompanheme.gestaosolicitacoes.model.enums.TipoEvento;
import br.com.acompanheme.gestaosolicitacoes.model.enums.Status;

public record SolicitacaoEvento(

    UUID solicitacaoId,
    TipoEvento tipoEvento,
    Status statusAtual,
    String motivo,
    Prioridade prioridadeAnterior,
    Prioridade prioridadeNova,
    String autor,
    String unidadeExecucaoNome,
    String unidadeExecucaoMunicipio,
    String nomePaciente,
    String emailPaciente,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime dataEvento
) {

}
