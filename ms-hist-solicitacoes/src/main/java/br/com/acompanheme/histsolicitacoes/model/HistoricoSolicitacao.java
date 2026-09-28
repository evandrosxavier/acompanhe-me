package br.com.acompanheme.histsolicitacoes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registro de auditoria de um evento de solicitação. Somente inserção:
 * sem setters, @Immutable (o Hibernate ignora alterações) e o repository não expõe update nem delete.
 */
@Entity
@Immutable
@Table(name = "historico_solicitacoes", indexes = {
        @Index(name = "idx_historico_solicitacao_id", columnList = "solicitacao_id"),
        @Index(name = "idx_historico_paciente_cpf", columnList = "paciente_cpf")
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HistoricoSolicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID solicitacaoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoEvento tipoEvento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusSolicitacao statusAtual;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Prioridade prioridadeAnterior;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Prioridade prioridadeNova;

    @Column(length = 100)
    private String autor;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(length = 150)
    private String unidadeExecucaoNome;

    @Column(length = 100)
    private String unidadeExecucaoMunicipio;

    @Column(length = 11)
    private String pacienteCpf;

    @Column(length = 150)
    private String pacienteNome;

    @Column(nullable = false)
    private LocalDateTime dataEvento;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataRegistro;
}
