package br.com.acompanheme.gestaosolicitacoes.controller.handler;

import br.com.acompanheme.gestaosolicitacoes.dto.ErroResponse;
import br.com.acompanheme.gestaosolicitacoes.excecoes.BusinessException;
import br.com.acompanheme.gestaosolicitacoes.excecoes.SolicitacaoNaoEncontradaException;
import br.com.acompanheme.gestaosolicitacoes.excecoes.TransicaoInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class ControllerExceptionHandler {

    private static final String BASE_URL = "https://api.acompanheme.com/errors/";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handlerBusinessException(BusinessException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(e.getHttpStatus(), e.getMessage());
        problemDetail.setTitle("Erro de Negócio");
        problemDetail.setType(URI.create(BASE_URL + "business-error"));
        return ResponseEntity.status(e.getHttpStatus()).body(problemDetail);
    }

    @ExceptionHandler(SolicitacaoNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> handlerSolicitacaoNaoEncontrada(SolicitacaoNaoEncontradaException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(TransicaoInvalidaException.class)
    public ResponseEntity<ErroResponse> handlerTransicaoInvalida(TransicaoInvalidaException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handlerMethodArgumentNotValid(MethodArgumentNotValidException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Erro de Validação");
        problemDetail.setDetail("Um ou mais campos falharam na validação.");
        problemDetail.setType(URI.create(BASE_URL + "validation-error"));
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map((FieldError error) -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        problemDetail.setProperty("validation_errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handlerMessageNotReadable(HttpMessageNotReadableException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Corpo da Requisição Inválido");
        problemDetail.setDetail("O corpo da requisição está malformado ou contém valor inválido (ex.: enum ou data fora do formato).");
        problemDetail.setType(URI.create(BASE_URL + "malformed-request"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }
}
