package com.example.backend.dto;

import com.example.backend.model.Cliente;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private Long id;

    @NotNull(message = "Tipo de pessoa é obrigatório")
    private Cliente.TipoPessoa tipoPessoa;

    @NotBlank(message = "CPF/CNPJ é obrigatório")
    private String cpfCnpj;

    private String nome;

    private String rg;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    private String razaoSocial;

    private String inscricaoEstadual;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataCriacao;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotNull(message = "Status ativo é obrigatório")
    private Boolean ativo;

    private List<EnderecoDTO> enderecos;
}