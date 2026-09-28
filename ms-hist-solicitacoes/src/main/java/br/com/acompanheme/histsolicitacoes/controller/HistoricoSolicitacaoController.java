package br.com.acompanheme.histsolicitacoes.controller;

import br.com.acompanheme.histsolicitacoes.dto.HistoricoSolicitacaoDTO;
import br.com.acompanheme.histsolicitacoes.service.HistoricoSolicitacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

// TODO: proteger as queries validando o JWT emitido pelo ms-gestao-consultas
//  (Spring Security removido deste módulo por ora).
@Controller
@RequiredArgsConstructor
public class HistoricoSolicitacaoController {

    private final HistoricoSolicitacaoService historicoService;

    @QueryMapping
    public List<HistoricoSolicitacaoDTO> historicoDaSolicitacao(@Argument UUID solicitacaoId) {
        return historicoService.historicoDaSolicitacao(solicitacaoId);
    }

    @QueryMapping
    public List<HistoricoSolicitacaoDTO> historicoDoPaciente(@Argument String cpf,
                                                             @Argument Integer page,
                                                             @Argument Integer size) {
        return historicoService.historicoDoPaciente(cpf, page, size);
    }
}
