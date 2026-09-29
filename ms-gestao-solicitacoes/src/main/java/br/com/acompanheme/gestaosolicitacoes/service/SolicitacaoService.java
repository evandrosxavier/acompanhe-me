package br.com.acompanheme.gestaosolicitacoes.service;

import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.AlterarPrioridadeRequest;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.AvaliarSolicitacaoRequest;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.CriarSolicitacaoRequest;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.DefinirLocalRequest;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.PosicaoFilaResponse;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.RegistrarRealizacaoRequest;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.ResponderOfertaRequest;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.SolicitacaoResponse;
import br.com.acompanheme.gestaosolicitacoes.excecoes.BusinessException;
import br.com.acompanheme.gestaosolicitacoes.excecoes.ErrorCode;
import br.com.acompanheme.gestaosolicitacoes.excecoes.SolicitacaoNaoEncontradaException;
import br.com.acompanheme.gestaosolicitacoes.excecoes.SolicitacaoSemPosicaoException;
import br.com.acompanheme.gestaosolicitacoes.mapper.SolicitacaoMapper;
import br.com.acompanheme.gestaosolicitacoes.model.domain.Solicitacao;
import br.com.acompanheme.gestaosolicitacoes.model.domain.UnidadeExecucao;
import br.com.acompanheme.gestaosolicitacoes.model.enums.Modalidade;
import br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade;
import br.com.acompanheme.gestaosolicitacoes.model.enums.Status;
import br.com.acompanheme.gestaosolicitacoes.model.enums.TipoEvento;
import br.com.acompanheme.gestaosolicitacoes.model.enums.TipoSolicitacao;
import br.com.acompanheme.gestaosolicitacoes.repository.SolicitacaoRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SolicitacaoService {

    private static final String AUTOR_PADRAO = "regulacao";

    private final SolicitacaoRepository solicitacaoRepository;

    private final ApplicationEventPublisher eventPublisher;

    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public UUID criar(Long consultaId, CriarSolicitacaoRequest request) {
        if (request.consultaId() != null && !request.consultaId().equals(consultaId)) {
            throw new BusinessException(ErrorCode.CONSULTA_ID_DIVERGENTE, HttpStatus.BAD_REQUEST);
        }
        validarModalidade(request.tipoSolicitacao(), request.modalidade());
        validarMotivoDaUrgencia(request.prioridade(), request.motivoDaUrgencia());
        Solicitacao solicitacao = SolicitacaoMapper.toEntity(request);
        solicitacao.setConsultaId(consultaId);
        solicitacao.setStatus(Status.REGISTRADA);
        Solicitacao salva = solicitacaoRepository.saveAndFlush(solicitacao);
        publicar(salva, TipoEvento.SOLICITACAO_REGISTRADA, null, null);
        return salva.getId();
    }

    @Transactional(readOnly = true)
    public SolicitacaoResponse buscarPorId(UUID id) {
        return SolicitacaoMapper.toResponse(buscarEntidade(id));
    }

    private void validarModalidade(TipoSolicitacao tipo, Modalidade modalidade) {
        if (tipo == TipoSolicitacao.CIRURGIA && modalidade == null) {
            throw new BusinessException(ErrorCode.MODALIDADE_OBRIGATORIA, HttpStatus.BAD_REQUEST);
        }
        if (tipo != TipoSolicitacao.CIRURGIA && modalidade != null) {
            throw new BusinessException(ErrorCode.MODALIDADE_NAO_PERMITIDA, HttpStatus.BAD_REQUEST);
        }
    }

    private void validarMotivoDaUrgencia(Prioridade prioridade, String motivoDaUrgencia) {
        if (prioridade == Prioridade.URGENTE && (motivoDaUrgencia == null || motivoDaUrgencia.isBlank())) {
            throw new BusinessException(ErrorCode.MOTIVO_URGENCIA_OBRIGATORIO, HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoResponse> listarFilaDeTrabalho(TipoSolicitacao tipo, Modalidade modalidade) {
        return solicitacaoRepository.buscarFilaDeTrabalho(tipo, modalidade).stream()
                .map(SolicitacaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoResponse> listarFilaDeEspera(TipoSolicitacao tipo, Modalidade modalidade) {
        // Cada modalidade de cirurgia é uma fila própria; por isso a modalidade segue a mesma regra da criação.
        validarModalidade(tipo, modalidade);
        return solicitacaoRepository.buscarFilaDeEsperaPorTipoEModalidade(tipo, modalidade).stream()
                .map(SolicitacaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PosicaoFilaResponse consultarPosicao(UUID id) {
        Solicitacao solicitacao = buscarEntidade(id);
        if (solicitacao.getStatus() != Status.EM_FILA) {
            throw new SolicitacaoSemPosicaoException(solicitacao.getStatus());
        }
        long naFrente = solicitacaoRepository.contarNaFrente(
                solicitacao.getTipoSolicitacao(),
                solicitacao.getModalidade(),
                solicitacao.getPrioridade().rank(),
                solicitacao.getDataEntradaFila());
        long tamanhoTotalFila = solicitacaoRepository.contarNaFila(
                solicitacao.getTipoSolicitacao(), solicitacao.getModalidade());
        return new PosicaoFilaResponse(solicitacao.getId(), naFrente + 1, tamanhoTotalFila);
    }

    @Transactional
    public SolicitacaoResponse iniciarAnalise(UUID id) {
        Solicitacao solicitacao = buscarEntidade(id);
        solicitacao.iniciarAnalise();
        return salvar(solicitacao, TipoEvento.SOLICITACAO_EM_ANALISE);
    }

    @Transactional
    public SolicitacaoResponse avaliar(UUID id, AvaliarSolicitacaoRequest request) {
        Solicitacao solicitacao = buscarEntidade(id);
        TipoEvento tipoEvento = switch (request.decisao()) {
            case APROVADA -> {
                solicitacao.avaliarComoAprovada();
                yield TipoEvento.SOLICITACAO_APROVADA;
            }
            case NEGADA -> {
                solicitacao.avaliarComoNegada(request.motivo());
                yield TipoEvento.SOLICITACAO_NEGADA;
            }
            case PENDENTE -> {
                solicitacao.avaliarComoPendente(request.motivo());
                yield TipoEvento.SOLICITACAO_DEVOLVIDA;
            }
        };
        return salvar(solicitacao, tipoEvento, request.motivo());
    }

    @Transactional
    public SolicitacaoResponse complementarDocumentacao(UUID id) {
        Solicitacao solicitacao = buscarEntidade(id);
        solicitacao.complementarDocumentacao();
        return salvar(solicitacao, TipoEvento.SOLICITACAO_EM_ANALISE);
    }

    @Transactional
    public SolicitacaoResponse alterarPrioridade(UUID id, AlterarPrioridadeRequest request) {
        Solicitacao solicitacao = buscarEntidade(id);
        Prioridade prioridadeAnterior = solicitacao.getPrioridade();
        solicitacao.alterarPrioridade(request.novaPrioridade(), AUTOR_PADRAO, request.justificativa());
        return salvar(solicitacao, TipoEvento.SOLICITACAO_PRIORIDADE_ALTERADA, request.justificativa(), prioridadeAnterior);
    }

    @Transactional
    public SolicitacaoResponse definirLocal(UUID id, DefinirLocalRequest request) {
        Solicitacao solicitacao = buscarEntidade(id);
        UnidadeExecucao unidadeExecucao = UnidadeExecucao.builder()
                .codigo(request.codigo())
                .nome(request.nome())
                .municipio(request.municipio())
                .bairro(request.bairro())
                .endereco(request.endereco())
                .ddd(request.ddd())
                .telefone(request.telefone())
                .build();
        solicitacao.definirLocal(unidadeExecucao);
        return salvar(solicitacao, TipoEvento.SOLICITACAO_LOCAL_DEFINIDO);
    }

    @Transactional
    public SolicitacaoResponse responderOferta(UUID id, ResponderOfertaRequest request) {
        Solicitacao solicitacao = buscarEntidade(id);
        TipoEvento tipoEvento;
        if (request.aceita()) {
            solicitacao.confirmarPeloPaciente();
            tipoEvento = TipoEvento.SOLICITACAO_OFERTA_ACEITA;
        } else {
            solicitacao.recusarOferta();
            tipoEvento = TipoEvento.SOLICITACAO_OFERTA_RECUSADA;
        }
        return salvar(solicitacao, tipoEvento);
    }

    @Transactional
    public SolicitacaoResponse cancelar(UUID id) {
        Solicitacao solicitacao = buscarEntidade(id);
        solicitacao.cancelar();
        return salvar(solicitacao, TipoEvento.SOLICITACAO_CANCELADA);
    }

    @Transactional
    public SolicitacaoResponse registrarRealizacao(UUID id, RegistrarRealizacaoRequest request) {
        Solicitacao solicitacao = buscarEntidade(id);
        solicitacao.registrarRealizacao(request != null ? request.dataAtendimento() : null);
        return salvar(solicitacao, TipoEvento.SOLICITACAO_CONCLUIDA);
    }

    private Solicitacao buscarEntidade(UUID id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(SolicitacaoNaoEncontradaException::new);
    }

    private SolicitacaoResponse salvar(Solicitacao solicitacao, TipoEvento tipoEvento) {
        return salvar(solicitacao, tipoEvento, null, null);
    }

    private SolicitacaoResponse salvar(Solicitacao solicitacao, TipoEvento tipoEvento, String motivo) {
        return salvar(solicitacao, tipoEvento, motivo, null);
    }

    private SolicitacaoResponse salvar(Solicitacao solicitacao, TipoEvento tipoEvento, String motivo,
                                       Prioridade prioridadeAnterior) {
        Solicitacao salva = solicitacaoRepository.saveAndFlush(solicitacao);
        publicar(salva, tipoEvento, motivo, prioridadeAnterior);
        return SolicitacaoMapper.toResponse(salva);
    }

    private void publicar(Solicitacao solicitacao, TipoEvento tipoEvento, String motivo, Prioridade prioridadeAnterior) {
        eventPublisher.publishEvent(
                SolicitacaoMapper.toEvento(solicitacao, tipoEvento, motivo, AUTOR_PADRAO, prioridadeAnterior));
    }
}
