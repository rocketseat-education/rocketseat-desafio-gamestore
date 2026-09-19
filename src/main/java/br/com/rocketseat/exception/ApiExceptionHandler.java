package br.com.rocketseat.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(DadosInvalidosException.class)
    public ProblemDetail dadosInvalidos(DadosInvalidosException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail naoEncontrado(RecursoNaoEncontradoException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ConflitoException.class)
    public ProblemDetail conflito(ConflitoException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integridade() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Operação em conflito com os dados cadastrados. Verifique e-mail ou compra duplicada.");
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ProblemDetail requisicaoInvalida() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Requisição inválida. Verifique o JSON, os campos obrigatórios e os tipos informados.");
    }
}
