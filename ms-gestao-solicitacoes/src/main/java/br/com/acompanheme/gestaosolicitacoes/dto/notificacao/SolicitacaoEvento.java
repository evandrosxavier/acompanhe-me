package br.com.acompanheme.gestaosolicitacoes.dto.notificacao;

import java.time.LocalDateTime;
import java.util.UUID;
import br.com.acompanheme.gestaosolicitacoes.model.enums.TipoEvento;
import br.com.acompanheme.gestaosolicitacoes.model.enums.Status;

public record SolicitacaoEvento(

    UUID solicitacaoId,
    TipoEvento tipoEvento,
    Status statusAtual,
    String motivo,
    String nomePaciente,
    String emailPaciente,
    LocalDateTime dataEvento
) {

}
