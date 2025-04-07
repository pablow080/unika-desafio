package org.desafioestagio.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando um cliente não é encontrado pelo ID informado
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClienteNaoEncontradoException extends RuntimeException {

    public ClienteNaoEncontradoException(Long id) {
        super(String.format("Cliente não encontrado com ID: %d", id));
    }

    public ClienteNaoEncontradoException(String cpfCnpj) {
        super(String.format("Cliente não encontrado com CPF/CNPJ: %s", cpfCnpj));
    }
}