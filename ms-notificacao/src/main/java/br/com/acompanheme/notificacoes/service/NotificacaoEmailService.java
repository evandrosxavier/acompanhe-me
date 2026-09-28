package br.com.acompanheme.notificacoes.service;

import br.com.acompanheme.notificacoes.dto.SolicitacaoNotificacaoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacaoEmailService {

    private final JavaMailSender mailSender;
    private final NotificacaoLogService logService;

    @Value("${spring.mail.username}")
    private String remetente;

    private static final String ASSUNTO = "Atualização da sua solicitação";
    private static final DateTimeFormatter FORMATTER_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    public void notificar(SolicitacaoNotificacaoDTO dto) {
        String fraseEvento = switch (dto.tipoEvento()) {
            case "SOLICITACAO_REGISTRADA" -> "Recebemos sua solicitação e ela já está registrada em nosso sistema.";
            case "SOLICITACAO_EM_ANALISE" -> "Sua solicitação está em análise pela equipe de regulação.";
            case "SOLICITACAO_DEVOLVIDA"  -> "Sua solicitação foi devolvida e precisa de ajustes da sua parte.";
            case "SOLICITACAO_NEGADA"     -> "Infelizmente, sua solicitação foi negada.";
            case "SOLICITACAO_APROVADA"   -> "Sua solicitação foi aprovada e entrou na fila de espera.";
            case "SOLICITACAO_PRIORIDADE_ALTERADA" -> "A prioridade da sua solicitação foi alterada de %s para %s."
                    .formatted(formatarPrioridade(dto.prioridadeAnterior()), formatarPrioridade(dto.prioridadeNova()));
            case "SOLICITACAO_LOCAL_DEFINIDO" -> dto.unidadeExecucaoNome() == null
                    ? "Foi definido um local para o seu atendimento."
                    : "Foi definido um local para o seu atendimento: %s - %s."
                            .formatted(dto.unidadeExecucaoNome(), dto.unidadeExecucaoMunicipio());
            case "SOLICITACAO_CANCELADA"  -> "Sua solicitação foi cancelada.";
            case "SOLICITACAO_CONCLUIDA"  -> "Sua solicitação foi concluída.";
            default -> null;
        };
        if (fraseEvento == null) {
            log.info("Evento {} não gera e-mail para o paciente | Solicitacao ID: {}",
                    dto.tipoEvento(), dto.solicitacaoId());
            return;
        }

        if (dto.emailPaciente() == null || dto.emailPaciente().isBlank()) {
            String mensagemErro = "E-mail do paciente ausente no evento";
            log.error("{} | Tipo: {} | Solicitacao ID: {}", mensagemErro, dto.tipoEvento(), dto.solicitacaoId());
            logService.registrarFalha(dto.solicitacaoId(), dto.tipoEvento(), dto.statusAtual(),
                    null, dto.nomePaciente(), ASSUNTO, mensagemErro);
            return;
        }

        enviar(dto, montarCorpo(dto, fraseEvento));
    }


    private String formatarPrioridade(String prioridade) {
        if (prioridade == null) return "não informada";
        return switch (prioridade) {
            case "URGENTE" -> "Urgente";
            case "ALTA"    -> "Alta";
            case "MEDIA"   -> "Média";
            case "BAIXA"   -> "Baixa";
            default -> prioridade;
        };
    }

    // O motivo pode conter texto interno da regulação (ex.: justificativa de prioridade),
    // então só vai ao paciente nos eventos em que ele precisa saber o porquê.
    private static final Set<String> EVENTOS_COM_MOTIVO = Set.of("SOLICITACAO_DEVOLVIDA", "SOLICITACAO_NEGADA");

    private String montarCorpo(SolicitacaoNotificacaoDTO dto, String fraseEvento) {
        boolean exibirMotivo = EVENTOS_COM_MOTIVO.contains(dto.tipoEvento())
                && dto.motivo() != null && !dto.motivo().isBlank();
        String linhaMotivo = exibirMotivo ? "\nMotivo: " + dto.motivo() + "\n" : "";

        return String.format("""
                Olá %s,

                %s
                %s
                Data da atualização: %s

                Em caso de dúvidas, entre em contato conosco.

                Atenciosamente,
                Equipe Regulação Saúde
                """,
                dto.nomePaciente(),
                fraseEvento,
                linhaMotivo,
                LocalDateTime.parse(dto.dataEvento()).format(FORMATTER_DATA_HORA));
    }

    private void enviar(SolicitacaoNotificacaoDTO dto, String corpo) {
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom(remetente);
            mensagem.setTo(dto.emailPaciente());
            mensagem.setSubject(ASSUNTO);
            mensagem.setText(corpo);
            mailSender.send(mensagem);

            log.info("E-mail enviado | Tipo: {} | Solicitacao ID: {}",
                    dto.tipoEvento(), dto.solicitacaoId());

            logService.registrarSucesso(dto.solicitacaoId(), dto.tipoEvento(), dto.statusAtual(),
                    dto.emailPaciente(), dto.nomePaciente(), ASSUNTO);

        } catch (Exception e) {
            Throwable causa = e.getCause() != null ? e.getCause() : e;
            String mensagemErro = causa.getMessage() != null ? causa.getMessage() : e.getMessage();

            log.error("Falha ao enviar e-mail | Tipo: {} | Solicitacao ID: {} | Erro: {}",
                    dto.tipoEvento(), dto.solicitacaoId(), mensagemErro);
            log.debug("Stack trace completo:", e);

            logService.registrarFalha(dto.solicitacaoId(), dto.tipoEvento(), dto.statusAtual(),
                    dto.emailPaciente(), dto.nomePaciente(), ASSUNTO, mensagemErro);
        }
    }

}


