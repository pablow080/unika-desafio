package org.desafioestagio.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando uma operação não é permitida devido a regras de negócio
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OperacaoNaoPermitidaException extends RuntimeException {

    public OperacaoNaoPermitidaException(String mensagem) {
        super(mensagem);
    }

    public OperacaoNaoPermitidaException(String entidade, String razao) {
        super(String.format("Operação não permitida para %s: %s", entidade, razao));
    }
}