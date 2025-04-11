package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoDTO {

    private Long id;

    @NotBlank(message = "Logradouro é obrigatório")
    private String logradouro;

    @NotBlank(message = "Número é obrigatório")
    private String numero;

    @NotBlank(message = "CEP é obrigatório")
    private String cep;

    @NotBlank(message = "Bairro é obrigatório")
    private String bairro;

    private String telefone;

    @NotBlank(message = "Cidade é obrigatório")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    private String estado;

    private String complemento;

    private Long clienteId;

    private boolean enderecoPrincipal;
}
