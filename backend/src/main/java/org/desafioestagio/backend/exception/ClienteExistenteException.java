package org.desafioestagio.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando se tenta cadastrar um cliente com CPF/CNPJ ou e-mail já existente
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ClienteExistenteException extends RuntimeException {

    public ClienteExistenteException(String campo, String valor) {
        super(String.format("Já existe um cliente cadastrado com %s: %s", campo, valor));
    }

    public ClienteExistenteException(String mensagem) {
        super(mensagem);
    }
}