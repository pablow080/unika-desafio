package org.desafioestagio.wicket.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Cliente implements Serializable {

    private Long id;
    private TipoPessoa tipoPessoa;
    private String cpfCnpj;
    private String nomeRazaoSocial;
    private String nome;
    private String razaoSocial;
    private String rgIe;
    private String rg;
    private String inscricaoEstadual;
    private LocalDate dataNascimentoDataCriacao;
    private LocalDate dataNascimento;
    private LocalDate dataCriacao;
    private String email;
    private boolean ativo;
    private boolean dadosConsistentes;

    private List<Endereco> enderecos = new ArrayList<>();
}
