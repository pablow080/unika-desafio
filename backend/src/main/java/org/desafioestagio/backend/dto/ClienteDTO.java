package org.desafioestagio.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.desafioestagio.backend.model.TipoPessoa;
import org.desafioestagio.backend.validation.PessoaFisica;
import org.desafioestagio.backend.validation.PessoaJuridica;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Setter
@Getter
public class ClienteDTO {
    // Getters e Setters
    private Long id;

    @NotNull(message = "Tipo de pessoa é obrigatório")
    private TipoPessoa tipoPessoa;

    @NotBlank(message = "CPF/CNPJ é obrigatório")
    @CPF(groups = PessoaFisica.class, message = "CPF inválido")
    @CNPJ(groups = PessoaJuridica.class, message = "CNPJ inválido")
    private String cpfCnpj;

    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @NotBlank(groups = PessoaFisica.class, message = "Nome é obrigatório para pessoa física")
    private String nome;

    @Size(max = 20, message = "RG deve ter no máximo 20 caracteres")
    private String rg;

    private LocalDate dataNascimento;

    @Size(max = 100, message = "Razão social deve ter no máximo 100 caracteres")
    @NotBlank(groups = PessoaJuridica.class, message = "Razão social é obrigatória para pessoa jurídica")
    private String razaoSocial;

    @Size(max = 20, message = "Inscrição estadual deve ter no máximo 20 caracteres")
    private String inscricaoEstadual;

    private LocalDate dataCriacao;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    private boolean ativo = true;

}