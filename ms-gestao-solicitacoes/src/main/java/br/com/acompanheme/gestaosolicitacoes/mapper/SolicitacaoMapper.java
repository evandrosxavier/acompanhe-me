package br.com.acompanheme.gestaosolicitacoes.mapper;

import br.com.acompanheme.gestaosolicitacoes.dto.notificacao.SolicitacaoEvento;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.CriarSolicitacaoRequest;
import br.com.acompanheme.gestaosolicitacoes.model.domain.Paciente;
import br.com.acompanheme.gestaosolicitacoes.model.domain.Solicitacao;
import br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade;
import br.com.acompanheme.gestaosolicitacoes.model.enums.TipoEvento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.SolicitacaoResponse;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.UnidadeExecucaoDTO;
import br.com.acompanheme.gestaosolicitacoes.model.domain.UnidadeExecucao;

public class SolicitacaoMapper {


    public static Solicitacao toEntity(CriarSolicitacaoRequest request) {
        if (request == null) {
            return null;
        }
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setConsultaId(request.consultaId());
        solicitacao.setPaciente(PacienteMapper.toEntity(request.paciente()));
        solicitacao.setMedico(MedicoMapper.toEntity(request.medico()));
        solicitacao.setUnidadeSolicitante(UnidadeSolicitanteMapper.toEntity(request.unidadeSolicitante()));
        solicitacao.setTipoSolicitacao(request.tipoSolicitacao());
        solicitacao.setProcedimentos(request.procedimentos().stream().map(ProcedimentoMapper::toEntity).collect(Collectors.toCollection(ArrayList::new)));
        solicitacao.setModalidade(request.modalidade());
        solicitacao.setPrioridade(request.prioridade());
        solicitacao.setMotivoDaUrgencia(request.motivoDaUrgencia());
        solicitacao.setDiagnostico(request.diagnostico());
        solicitacao.setObservacoes(request.observacoes());
        return solicitacao;
    }

    public static SolicitacaoResponse toResponse(Solicitacao s) {
        if (s == null) {
            return null;
        }
        return new SolicitacaoResponse(
                s.getId(),
                s.getConsultaId(),
                PacienteMapper.toDTO(s.getPaciente()),
                MedicoMapper.toDTO(s.getMedico()),
                UnidadeSolicitanteMapper.toDTO(s.getUnidadeSolicitante()),
                s.getTipoSolicitacao(),
                s.getProcedimentos().stream().map(ProcedimentoMapper::toDTO).toList(),
                s.getModalidade(),
                s.getPrioridade(),
                s.getMotivoDaUrgencia(),
                s.getDiagnostico(),
                s.getObservacoes(),
                s.getParecerDaRegulacao(),
                s.getStatus(),
                toDTO(s.getUnidadeExecucao()),
                s.getDataSolicitacao(),
                s.getDataAtualizacao(),
                s.getDataEntradaFila(),
                s.getDataAtendimento());
    }

    /**
     * @param prioridadeAnterior preenchido apenas em alterações de prioridade; nesse caso a
     *                           prioridade atual da solicitação vai como prioridadeNova.
     */
    public static SolicitacaoEvento toEvento(Solicitacao s, TipoEvento tipoEvento, String motivo,
                                             String autor, Prioridade prioridadeAnterior) {
        if (s == null) {
            return null;
        }
        Paciente paciente = s.getPaciente();
        UnidadeExecucao unidade = s.getUnidadeExecucao();
        return new SolicitacaoEvento(
                s.getId(),
                tipoEvento,
                s.getStatus(),
                motivo,
                prioridadeAnterior,
                prioridadeAnterior != null ? s.getPrioridade() : null,
                autor,
                unidade != null ? unidade.getNome() : null,
                unidade != null ? unidade.getMunicipio() : null,
                paciente != null ? paciente.getCpf() : null,
                paciente != null ? paciente.getNome() : null,
                paciente != null ? paciente.getEmail() : null,
                LocalDateTime.now()
        );
    }

    private static UnidadeExecucaoDTO toDTO(UnidadeExecucao u) {
        if (u == null) {
            return null;
        }
        return new UnidadeExecucaoDTO(u.getCodigo(), u.getNome(), u.getMunicipio(), u.getBairro(),
                u.getEndereco(), u.getDdd(), u.getTelefone());
    }
}
