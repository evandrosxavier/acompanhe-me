package br.com.acompanheme.histsolicitacoes.service;

import br.com.acompanheme.histsolicitacoes.dto.HistoricoSolicitacaoDTO;
import br.com.acompanheme.histsolicitacoes.dto.SolicitacaoEventoDTO;
import br.com.acompanheme.histsolicitacoes.excecoes.BusinessException;
import br.com.acompanheme.histsolicitacoes.excecoes.ErrorCode;
import br.com.acompanheme.histsolicitacoes.model.HistoricoSolicitacao;
import br.com.acompanheme.histsolicitacoes.model.Prioridade;
import br.com.acompanheme.histsolicitacoes.model.StatusSolicitacao;
import br.com.acompanheme.histsolicitacoes.model.TipoEvento;
import br.com.acompanheme.histsolicitacoes.repository.HistoricoSolicitacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoricoSolicitacaoService {

    private static final int PAGE_PADRAO = 0;
    private static final int SIZE_PADRAO = 10;
    private static final int SIZE_MAXIMO = 100;

    private final HistoricoSolicitacaoRepository repository;

    @Transactional
    public void registrar(SolicitacaoEventoDTO evento) {
        // valueOf/parse lançam exceção se o evento vier fora do contrato: o error handler
        // tenta de novo e, persistindo o erro, envia a mensagem para a DLT.
        HistoricoSolicitacao historico = HistoricoSolicitacao.builder()
                .solicitacaoId(evento.solicitacaoId())
                .tipoEvento(TipoEvento.valueOf(evento.tipoEvento()))
                .statusAtual(StatusSolicitacao.valueOf(evento.statusAtual()))
                .prioridadeAnterior(evento.prioridadeAnterior() != null ? Prioridade.valueOf(evento.prioridadeAnterior()) : null)
                .prioridadeNova(evento.prioridadeNova() != null ? Prioridade.valueOf(evento.prioridadeNova()) : null)
                .autor(evento.autor())
                .motivo(evento.motivo())
                .unidadeExecucaoNome(evento.unidadeExecucaoNome())
                .unidadeExecucaoMunicipio(evento.unidadeExecucaoMunicipio())
                .pacienteCpf(normalizarCpf(evento.cpfPaciente()))
                .pacienteNome(evento.nomePaciente())
                .dataEvento(LocalDateTime.parse(evento.dataEvento()))
                .build();
        repository.save(historico);
        log.info("Histórico gravado | Tipo: {} | Solicitacao ID: {}", evento.tipoEvento(), evento.solicitacaoId());
    }

    @Transactional(readOnly = true)
    public List<HistoricoSolicitacaoDTO> historicoDaSolicitacao(UUID solicitacaoId) {
        return repository.findBySolicitacaoIdOrderByDataEventoAscDataRegistroAsc(solicitacaoId).stream()
                .map(HistoricoSolicitacaoDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HistoricoSolicitacaoDTO> historicoDoPaciente(String cpf, Integer page, Integer size) {
        String cpfNormalizado = normalizarCpf(cpf);
        if (cpfNormalizado == null || cpfNormalizado.length() != 11) {
            throw new BusinessException(ErrorCode.CPF_INVALIDO, HttpStatus.BAD_REQUEST);
        }
        int pagina = page != null ? page : PAGE_PADRAO;
        int tamanho = size != null ? size : SIZE_PADRAO;
        if (pagina < 0 || tamanho < 1 || tamanho > SIZE_MAXIMO) {
            throw new BusinessException(ErrorCode.PAGINACAO_INVALIDA, HttpStatus.BAD_REQUEST);
        }
        Sort maisRecentePrimeiro = Sort.by(Sort.Order.desc("dataEvento"), Sort.Order.desc("dataRegistro"));
        return repository.findByPacienteCpf(cpfNormalizado, PageRequest.of(pagina, tamanho, maisRecentePrimeiro))
                .map(HistoricoSolicitacaoDTO::fromEntity)
                .getContent();
    }

    private String normalizarCpf(String cpf) {
        if (cpf == null) return null;
        return cpf.replaceAll("[^0-9]", "");
    }
}
