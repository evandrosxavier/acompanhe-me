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
import java.util.Arrays;
import java.util.stream.Collectors;

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
            case "SOLICITACAO_DEVOLVIDA"  -> "Sua solicitação foi devolvida e precisa de ajustes da sua parte.";
            case "SOLICITACAO_CONFIRMADA" -> "Sua solicitação foi confirmada.";
            case "SOLICITACAO_REJEITADA"  -> "Infelizmente, sua solicitação foi rejeitada.";
            case "SOLICITACAO_CANCELADA"  -> "Sua solicitação foi cancelada.";
            case "SOLICITACAO_CONCLUIDA"  -> "Sua solicitação foi concluída.";
            default -> null;
        };
              if (fraseEvento == null) {
            log.info("Evento {} não gera e-mail para o paciente | Solicitacao ID: {}",
                    dto.tipoEvento(), dto.solicitacaoId());
            return;
        }

        enviar(dto, montarCorpo(dto, fraseEvento));
    }


    private String montarCorpo(SolicitacaoNotificacaoDTO dto, String fraseEvento) {
        String linhaMotivo = (dto.motivo() == null || dto.motivo().isBlank())
                ? ""
                : "\nMotivo: " + dto.motivo() + "\n";

        return String.format("""
                Olá %s,

                %s
                %s
                Data da atualização: %s

                Em caso de dúvidas, entre em contato conosco.

                Atenciosamente,
                Equipe Agende-me
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


