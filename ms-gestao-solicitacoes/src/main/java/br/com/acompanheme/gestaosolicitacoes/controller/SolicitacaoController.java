package br.com.acompanheme.gestaosolicitacoes.controller;

import br.com.acompanheme.gestaosolicitacoes.dto.ErroResponse;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.AlterarPrioridadeRequest;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.AvaliarSolicitacaoRequest;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.CriarSolicitacaoRequest;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.DefinirLocalRequest;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.PosicaoFilaResponse;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.ResponderOfertaRequest;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.SolicitacaoCriadaResponse;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.SolicitacaoResponse;
import br.com.acompanheme.gestaosolicitacoes.model.enums.Modalidade;
import br.com.acompanheme.gestaosolicitacoes.model.enums.TipoSolicitacao;
import br.com.acompanheme.gestaosolicitacoes.service.SolicitacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Solicitações", description = "Operações de gerenciamento de solicitações")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    @Operation(summary = "Criar solicitação", description = "Registra uma nova solicitação (consulta, exame ou cirurgia) vinculada à consulta do path, com status REGISTRADA. Retorna apenas o id criado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitação criada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolicitacaoCriadaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou consultaId do corpo diferente do path",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/consultas/{consultaId}/solicitacoes")
    public ResponseEntity<SolicitacaoCriadaResponse> criar(@PathVariable Long consultaId,
                                                           @RequestBody @Valid CriarSolicitacaoRequest request,
                                                           UriComponentsBuilder uriBuilder) {
        UUID id = solicitacaoService.criar(consultaId, request);
        var uri = uriBuilder.path("/solicitacoes/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(uri).body(new SolicitacaoCriadaResponse(id));
    }

    @Operation(summary = "Buscar solicitação por ID", description = "Retorna os dados de uma solicitação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolicitacaoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/solicitacoes/{id}")
    public ResponseEntity<SolicitacaoResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitacaoService.buscarPorId(id));
    }

    @Operation(summary = "Listar fila de trabalho", description = "Lista as solicitações REGISTRADA aguardando análise da regulação, do tipo e modalidade informados, ordenadas por prioridade (URGENTE primeiro) e data da solicitação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SolicitacaoResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Parâmetro tipo ausente ou valor de enum inválido",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/solicitacoes/fila-trabalho")
    public ResponseEntity<List<SolicitacaoResponse>> listarFilaDeTrabalho(
            @Parameter(description = "Tipo da solicitação", required = true) @RequestParam TipoSolicitacao tipo,
            @Parameter(description = "Modalidade da solicitação (opcional; se omitida, lista todas as modalidades)") @RequestParam(required = false) Modalidade modalidade) {
        return ResponseEntity.ok(solicitacaoService.listarFilaDeTrabalho(tipo, modalidade));
    }

    @Operation(summary = "Listar fila de espera", description = "Lista as solicitações EM_FILA do tipo e modalidade informados, na ordem em que serão atendidas: prioridade (URGENTE primeiro) e data de entrada na fila.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SolicitacaoResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Parâmetro tipo ausente, valor de enum inválido, modalidade ausente para CIRURGIA ou informada para CONSULTA/EXAME",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/solicitacoes/fila-espera")
    public ResponseEntity<List<SolicitacaoResponse>> listarFilaDeEspera(
            @Parameter(description = "Tipo da solicitação", required = true) @RequestParam TipoSolicitacao tipo,
            @Parameter(description = "Modalidade: obrigatória quando o tipo é CIRURGIA (cada modalidade é uma fila) e não permitida para CONSULTA ou EXAME") @RequestParam(required = false) Modalidade modalidade) {
        return ResponseEntity.ok(solicitacaoService.listarFilaDeEspera(tipo, modalidade));
    }

    @Operation(summary = "Consultar posição na fila", description = "Retorna a posição da solicitação na fila de espera do seu tipo e modalidade, e o tamanho total dessa fila. Válido apenas para solicitações EM_FILA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PosicaoFilaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Solicitação não está em fila de espera no momento",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/solicitacoes/{id}/posicao")
    public ResponseEntity<PosicaoFilaResponse> consultarPosicao(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitacaoService.consultarPosicao(id));
    }

    @Operation(summary = "Iniciar análise", description = "Um analista da regulação assume a solicitação para começar a avaliar. Válido a partir de REGISTRADA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação em análise",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolicitacaoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Transição inválida a partir do status atual",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/solicitacoes/{id}/analise")
    public ResponseEntity<SolicitacaoResponse> iniciarAnalise(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitacaoService.iniciarAnalise(id));
    }

    @Operation(summary = "Avaliar solicitação", description = "Registra a decisão da regulação (aprovada, negada ou pendente de documentação) para uma solicitação em análise.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação avaliada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolicitacaoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (ex.: motivo ausente para NEGADA ou PENDENTE)",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Transição inválida a partir do status atual",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/solicitacoes/{id}/avaliacao")
    public ResponseEntity<SolicitacaoResponse> avaliar(@PathVariable UUID id,
                                                        @RequestBody @Valid AvaliarSolicitacaoRequest request) {
        return ResponseEntity.ok(solicitacaoService.avaliar(id, request));
    }

    @Operation(summary = "Complementar documentação", description = "Sinaliza que a documentação pendente foi complementada, retornando a solicitação para análise.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação de volta em análise",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolicitacaoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Transição inválida a partir do status atual",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PatchMapping("/solicitacoes/{id}/complemento")
    public ResponseEntity<SolicitacaoResponse> complementarDocumentacao(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitacaoService.complementarDocumentacao(id));
    }

    @Operation(summary = "Alterar prioridade", description = "Altera a prioridade de uma solicitação em fila de espera, sem mudar seu status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prioridade alterada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolicitacaoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (ex.: justificativa ausente)",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Transição inválida a partir do status atual",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PatchMapping("/solicitacoes/{id}/prioridade")
    public ResponseEntity<SolicitacaoResponse> alterarPrioridade(@PathVariable UUID id,
                                                                  @RequestBody @Valid AlterarPrioridadeRequest request) {
        return ResponseEntity.ok(solicitacaoService.alterarPrioridade(id, request));
    }

    @Operation(summary = "Definir local de execução", description = "Define a unidade de execução ofertada ao paciente para uma solicitação em fila. Válido a partir de EM_FILA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Local definido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolicitacaoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Transição inválida a partir do status atual",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/solicitacoes/{id}/oferta")
    public ResponseEntity<SolicitacaoResponse> definirLocal(@PathVariable UUID id,
                                                             @RequestBody @Valid DefinirLocalRequest request) {
        return ResponseEntity.ok(solicitacaoService.definirLocal(id, request));
    }

    @Operation(summary = "Responder oferta", description = "Registra a resposta do paciente ao local ofertado: confirma (LOCAL_DEFINIDO -> CONFIRMADA) ou recusa (LOCAL_DEFINIDO -> EM_FILA).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Oferta respondida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolicitacaoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Transição inválida a partir do status atual",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/solicitacoes/{id}/resposta")
    public ResponseEntity<SolicitacaoResponse> responderOferta(@PathVariable UUID id,
                                                                @RequestBody @Valid ResponderOfertaRequest request) {
        return ResponseEntity.ok(solicitacaoService.responderOferta(id, request));
    }

    @Operation(summary = "Cancelar solicitação", description = "Muda o status da solicitação para CANCELADA, preservando o histórico. Inválido para solicitações CONFIRMADA, CONCLUIDA ou já CANCELADA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação cancelada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolicitacaoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Solicitação já confirmada, concluída ou cancelada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/solicitacoes/{id}/cancelamento")
    public ResponseEntity<SolicitacaoResponse> cancelar(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitacaoService.cancelar(id));
    }

    @Operation(summary = "Registrar realização", description = "Registra que o atendimento foi realizado, concluindo a solicitação. Válido a partir de CONFIRMADA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação concluída",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolicitacaoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Transição inválida a partir do status atual",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/solicitacoes/{id}/realizacao")
    public ResponseEntity<SolicitacaoResponse> registrarRealizacao(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitacaoService.registrarRealizacao(id));
    }
}
