package org.desafioestagio.wicket.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Endereco implements Serializable {

    private Long id;
    private String logradouro;
    private String numero;
    private String cep;
    private String bairro;
    private String telefone;
    private String cidade;
    private String estado;
    private String complemento;
    private Long clienteId;
    private boolean enderecoPrincipal;

    // Getters e Setters

    public String getEnderecoCompleto() {
        return logradouro + ", " + numero + (complemento != null && !complemento.isEmpty() ? " - " + complemento : "") +
                " - " + bairro + " - " + cidade + "/" + estado + " - CEP: " + cep;
    }
}
