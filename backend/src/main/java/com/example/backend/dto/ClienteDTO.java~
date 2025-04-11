package com.example.backend.dto;

import com.example.backend.model.Cliente;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteDTO {

    @Schema(description = "ID do cliente", example = "1")
    private Long id;

    @NotNull(message = "Tipo de pessoa é obrigatório")
    @Schema(description = "Tipo de pessoa", example = "FISICA")
    private Cliente.TipoPessoa tipoPessoa;

    @NotBlank(message = "CPF/CNPJ é obrigatório")
    @Schema(description = "CPF ou CNPJ do cliente", example = "123.456.789-09")
    private String cpfCnpj;

    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Schema(description = "Nome completo (para pessoa física)", example = "João da Silva")
    private String nome;

    @Size(max = 20, message = "RG deve ter no máximo 20 caracteres")
    @Schema(description = "Número do RG", example = "12.345.678-9")
    private String rg;

    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate dataNascimento;

    @Size(max = 100, message = "Razão social deve ter no máximo 100 caracteres")
    @Schema(description = "Razão social (para pessoa jurídica)", example = "Empresa XYZ Ltda")
    private String razaoSocial;

    @Size(max = 20, message = "Inscrição estadual deve ter no máximo 20 caracteres")
    @Schema(description = "Inscrição estadual (para pessoa jurídica)", example = "123.456.789")
    private String inscricaoEstadual;

    private LocalDate dataCriacao;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 100, message = "E-mail deve ter no máximo 100 caracteres")
    @Schema(description = "E-mail do cliente", example = "cliente@example.com")
    private String email;

    @NotNull(message = "Status ativo é obrigatório")
    @Schema(description = "Indica se o cliente está ativo", example = "true")
    private Boolean ativo;

    @Valid
    @Schema(description = "Lista de endereços do cliente")
    private List<EnderecoDTO> enderecos;

    // Validação condicional
    @AssertTrue(message = "Dados inconsistentes para o tipo de pessoa")
    public boolean isDadosConsistentes() {
        if (tipoPessoa == null) return false;

        if (tipoPessoa == Cliente.TipoPessoa.FISICA) {
            return nome != null && !nome.isBlank() &&
                    (razaoSocial == null || razaoSocial.isBlank());
        } else {
            return razaoSocial != null && !razaoSocial.isBlank() &&
                    (nome == null || nome.isBlank());
        }
    }
}