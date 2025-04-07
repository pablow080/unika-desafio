package org.desafioestagio.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando um endereço não é encontrado pelo ID informado
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EnderecoNaoEncontradoException extends RuntimeException {

    public EnderecoNaoEncontradoException(Long id) {
        super(String.format("Endereço não encontrado com ID: %d", id));
    }
}