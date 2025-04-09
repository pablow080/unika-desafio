package org.desafioestagio.wicket.model;

import java.io.Serializable;

public class Endereco implements Serializable {
    private String id;
    private String telefone;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private boolean enderecoPrincipal;  // Renomeado para camelCase

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getLogradouro() { return logradouro; }  // Método renomeado para manter consistência
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public boolean isEnderecoPrincipal() { return enderecoPrincipal; }  // Renomeado para camelCase
    public void setEnderecoprincipal(boolean enderecoprincipal) { this.enderecoPrincipal = enderecoprincipal; }

    // Método para formatar o endereço completo
    public String getEnderecoCompleto() {
        return String.format("%s, %s%s, %s - %s/%s - %s - %s - %s - %b",
                id,
                numero,
                (complemento != null && !complemento.isEmpty() ? " " + complemento : ""),
                bairro,
                cidade,
                estado,
                cep,
                telefone,
                logradouro,
                enderecoPrincipal);
    }
}
