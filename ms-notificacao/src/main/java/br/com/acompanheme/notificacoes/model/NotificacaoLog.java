package br.com.acompanheme.notificacoes.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificacoes_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacaoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID solicitacaoId;

    @Column(nullable = false)
    private String tipoEvento;

    // Opcionais de propósito: se o evento chegar sem esses dados,
    // a falha ainda precisa conseguir ser registrada.
    @Column
    private String destinatario;

    @Column
    private String pacienteNome;

    @Column(nullable = false)
    private String assunto;

    @Column(length = 30)
    private String statusSolicitacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StatusEnvio status;

    @Column(columnDefinition = "TEXT")
    private String mensagemErro;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataEnvio;
}

