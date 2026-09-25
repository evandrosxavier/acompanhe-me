package br.com.acompanheme.gestaosolicitacoes.model.domain;

import br.com.acompanheme.gestaosolicitacoes.excecoes.TransicaoInvalidaException;
import br.com.acompanheme.gestaosolicitacoes.model.enums.Modalidade;
import br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade;
import br.com.acompanheme.gestaosolicitacoes.model.enums.Status;
import br.com.acompanheme.gestaosolicitacoes.model.enums.TipoSolicitacao;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_solicitacao")
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Long consultaId;

    @Embedded
    private Paciente paciente;

    @Embedded
    private Medico medico;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "codigo", column = @Column(name = "unidade_solicitante_codigo", nullable = false, length = 20)),
            @AttributeOverride(name = "nome", column = @Column(name = "unidade_solicitante_nome", nullable = false, length = 150)),
            @AttributeOverride(name = "municipio", column = @Column(name = "unidade_solicitante_municipio", nullable = false, length = 100)),
            @AttributeOverride(name = "bairro", column = @Column(name = "unidade_solicitante_bairro", nullable = false, length = 100))
    })
    private UnidadeSolicitante unidadeSolicitante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoSolicitacao tipoSolicitacao;

    @ElementCollection
    @CollectionTable(name = "tb_solicitacao_procedimento", joinColumns = @JoinColumn(name = "solicitacao_id"))
    @Builder.Default
    private List<Procedimento> procedimentos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Modalidade modalidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Prioridade prioridade;

    @Column(columnDefinition = "TEXT")
    private String motivoDaUrgencia;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(columnDefinition = "TEXT")
    private String parecerDaRegulacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Status status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "codigo", column = @Column(name = "unidade_execucao_codigo", length = 20)),
            @AttributeOverride(name = "nome", column = @Column(name = "unidade_execucao_nome", length = 150)),
            @AttributeOverride(name = "municipio", column = @Column(name = "unidade_execucao_municipio", length = 100)),
            @AttributeOverride(name = "bairro", column = @Column(name = "unidade_execucao_bairro", length = 100)),
            @AttributeOverride(name = "endereco", column = @Column(name = "unidade_execucao_endereco", length = 255)),
            @AttributeOverride(name = "ddd", column = @Column(name = "unidade_execucao_ddd", length = 3)),
            @AttributeOverride(name = "telefone", column = @Column(name = "unidade_execucao_telefone", length = 20))
    })
    private UnidadeExecucao unidadeExecucao;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataSolicitacao;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    private LocalDateTime dataEntradaFila;

    private LocalDateTime dataAtendimento;

    public void iniciarAnalise() {
        if (status != Status.REGISTRADA) {
            throw TransicaoInvalidaException.para(status, "iniciarAnalise");
        }
        status = Status.EM_ANALISE;
        dataAtualizacao = LocalDateTime.now();
    }

    public void avaliarComoPendente(String motivo) {
        if (status != Status.EM_ANALISE) {
            throw TransicaoInvalidaException.para(status, "avaliarComoPendente");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo é obrigatório para marcar a solicitação como pendente de documentação.");
        }
        status = Status.PENDENTE_DOCUMENTACAO;
        parecerDaRegulacao = motivo;
        dataAtualizacao = LocalDateTime.now();
    }

    public void complementarDocumentacao() {
        if (status != Status.PENDENTE_DOCUMENTACAO) {
            throw TransicaoInvalidaException.para(status, "complementarDocumentacao");
        }
        status = Status.EM_ANALISE;
        dataAtualizacao = LocalDateTime.now();
    }

    public void avaliarComoNegada(String motivo) {
        if (status != Status.EM_ANALISE) {
            throw TransicaoInvalidaException.para(status, "avaliarComoNegada");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo é obrigatório para negar a solicitação.");
        }
        status = Status.NEGADA;
        parecerDaRegulacao = motivo;
        dataAtualizacao = LocalDateTime.now();
    }

    public void avaliarComoAprovada() {
        if (status != Status.EM_ANALISE) {
            throw TransicaoInvalidaException.para(status, "avaliarComoAprovada");
        }
        status = Status.EM_FILA;
        dataEntradaFila = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    public void alterarPrioridade(Prioridade novaPrioridade, String autor, String justificativa) {
        if (status != Status.EM_FILA) {
            throw TransicaoInvalidaException.para(status, "alterarPrioridade");
        }
        if (justificativa == null || justificativa.isBlank()) {
            throw new IllegalArgumentException("Justificativa é obrigatória para alterar a prioridade.");
        }
        // TODO: publicar evento de auditoria com o autor desta alteração quando o mecanismo de auditoria existir.
        prioridade = novaPrioridade;
        dataAtualizacao = LocalDateTime.now();
    }

    public void definirLocal(UnidadeExecucao unidadeExecucao) {
        if (status != Status.EM_FILA) {
            throw TransicaoInvalidaException.para(status, "definirLocal");
        }
        this.unidadeExecucao = unidadeExecucao;
        status = Status.LOCAL_DEFINIDO;
        dataAtualizacao = LocalDateTime.now();
    }

    public void confirmarPeloPaciente() {
        if (status != Status.LOCAL_DEFINIDO) {
            throw TransicaoInvalidaException.para(status, "confirmarPeloPaciente");
        }
        status = Status.CONFIRMADA;
        dataAtualizacao = LocalDateTime.now();
    }

    public void recusarOferta() {
        if (status != Status.LOCAL_DEFINIDO) {
            throw TransicaoInvalidaException.para(status, "recusarOferta");
        }
        status = Status.EM_FILA;
        dataAtualizacao = LocalDateTime.now();
    }

    public void cancelar() {
        if (status == Status.CONFIRMADA || status == Status.CONCLUIDA || status == Status.CANCELADA) {
            throw TransicaoInvalidaException.para(status, "cancelar");
        }
        status = Status.CANCELADA;
        dataAtualizacao = LocalDateTime.now();
    }

    public void registrarRealizacao() {
        if (status != Status.CONFIRMADA) {
            throw TransicaoInvalidaException.para(status, "registrarRealizacao");
        }
        status = Status.CONCLUIDA;
        dataAtendimento = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }
}
