package org.desafioestagio.wicket.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Cliente implements Serializable {

    private Long id;
    private TipoPessoa tipoPessoa;  // Alterado para ser do tipo TipoPessoa (enum)
    private String cpfCnpj;
    private String nome;
    private String razaoSocial;
    private String rg;                    // Física
    private String inscricaoEstadual;
    private LocalDate dataCriacao;        // Jurídica
    private LocalDate dataNascimento;     // Física
    private String email;
    private boolean ativo;
    private List<Endereco> enderecos;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // Alterado o tipo de retorno para TipoPessoa (não mais double)
    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    // Alterado para aceitar TipoPessoa (não mais double)
    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) {
        if (isValidCpfCnpj(cpfCnpj)) {
            this.cpfCnpj = cpfCnpj;
        } else {
            throw new IllegalArgumentException("CPF/CNPJ inválido");
        }
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }

    public String getRg() { return rg; }
    public void setRg(String rg) {
        if (isValidRg(rg)) {
            this.rg = rg;
        } else {
            throw new IllegalArgumentException("RG inválido");
        }
    }

    public String getInscricaoEstadual() { return inscricaoEstadual; }
    public void setInscricaoEstadual(String inscricaoEstadual) {
        if (isValidInscricaoEstadual(inscricaoEstadual)) {
            this.inscricaoEstadual = inscricaoEstadual;
        } else {
            throw new IllegalArgumentException("Inscrição Estadual inválida");
        }
    }

    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public List<Endereco> getEnderecos() { return enderecos; }
    public void setEnderecos(List<Endereco> enderecos) { this.enderecos = enderecos; }

    public String getTelefonePrincipal() {
        return enderecos != null && !enderecos.isEmpty()
                ? enderecos.get(0).getTelefone()
                : "";
    }

    public String getNomeOuRazao() {
        return TipoPessoa.JURIDICA.equals(tipoPessoa) && razaoSocial != null
                ? razaoSocial
                : nome;
    }

    public IModel<String> getRgIe() {
        if (TipoPessoa.JURIDICA.equals(tipoPessoa)) {
            return Model.of(inscricaoEstadual != null ? inscricaoEstadual : "");
        } else {
            return Model.of(rg != null ? rg : "");
        }
    }

    // Método toString() sobrescrito para retornar uma representação amigável do Cliente
    @Override
    public String toString() {
        return nome != null ? nome : razaoSocial;
    }

    // Sobrescrita do equals() e hashCode() baseados no ID do Cliente
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cliente cliente = (Cliente) obj;
        return id != null && id.equals(cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Validação de CPF/CNPJ
    private boolean isValidCpfCnpj(String cpfCnpj) {
        return cpfCnpj != null && cpfCnpj.matches("\\d{11}|\\d{14}"); // Exemplo simples
    }

    // Validação de RG
    private boolean isValidRg(String rg) {
        return rg != null && rg.matches("\\d+");
    }

    // Validação de Inscrição Estadual
    private boolean isValidInscricaoEstadual(String inscricaoEstadual) {
        return inscricaoEstadual != null && inscricaoEstadual.matches("\\d+");
    }

    public LocalDate getDataReferencia() {
        return TipoPessoa.JURIDICA.equals(tipoPessoa) ? dataCriacao : dataNascimento;
    }

    public String getDataReferenciaFormatada() {
        LocalDate data = getDataReferencia();
        return data != null ? data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public IModel<String> getDataReferenciaModel() {
        return Model.of(getDataReferenciaFormatada());
    }
}
