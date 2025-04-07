package org.desafioestagio.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.desafioestagio.backend.model.TipoPessoa;
import org.desafioestagio.backend.validation.PessoaFisica;
import org.desafioestagio.backend.validation.PessoaJuridica;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO implements Serializable {

    public interface OnCreate {}

    private Long id;

    @NotNull(message = "Tipo de pessoa é obrigatório")
    private TipoPessoa tipoPessoa;

    @NotBlank(groups = OnCreate.class, message = "CPF/CNPJ é obrigatório")
    private String cpfCnpj;

    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @NotBlank(groups = PessoaFisica.class, message = "Nome é obrigatório para pessoa física")
    private String nome;

    @Size(max = 20, message = "RG deve ter no máximo 20 caracteres")
    @NotBlank(groups = PessoaFisica.class, message = "RG é obrigatório para pessoa física")
    private String rg;

    @PastOrPresent(message = "Data de nascimento não pode ser futura")
    private LocalDate dataNascimento;

    @Size(max = 100, message = "Razão social deve ter no máximo 100 caracteres")
    @NotBlank(groups = PessoaJuridica.class, message = "Razão social é obrigatória para pessoa jurídica")
    private String razaoSocial;

    @Size(max = 20, message = "Inscrição estadual deve ter no máximo 20 caracteres")
    private String inscricaoEstadual;

    @PastOrPresent(message = "Data de criação não pode ser futura")
    private LocalDate dataCriacao;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    private boolean ativo = true;

    @Valid
    private List<EnderecoDTO> enderecos;

    @AssertTrue(message = "Data de criação é obrigatória para pessoa jurídica")
    private boolean isDataCriacaoValid() {
        return tipoPessoa != TipoPessoa.JURIDICA || dataCriacao != null;
    }

    @JsonIgnore
    private boolean cpfCnpjValid;

    @AssertTrue(message = "CPF/CNPJ inválido")
    public boolean isCpfCnpjValid() {
        if (cpfCnpj == null || tipoPessoa == null) {
            return false;
        }

        String documento = cpfCnpj.replaceAll("[^\\d]", "");

        if (tipoPessoa == TipoPessoa.FISICA) {
            return isValidCPF(documento);
        } else if (tipoPessoa == TipoPessoa.JURIDICA) {
            return isValidCNPJ(documento);
        }
        return false;
    }

    private boolean isValidCPF(String cpf) {
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) return false;

        try {
            int d1 = 0, d2 = 0;
            for (int i = 0; i < 9; i++) {
                int dig = Character.getNumericValue(cpf.charAt(i));
                d1 += dig * (10 - i);
                d2 += dig * (11 - i);
            }

            d1 = 11 - (d1 % 11);
            d1 = (d1 >= 10) ? 0 : d1;
            d2 += d1 * 2;
            d2 = 11 - (d2 % 11);
            d2 = (d2 >= 10) ? 0 : d2;

            return d1 == Character.getNumericValue(cpf.charAt(9)) &&
                    d2 == Character.getNumericValue(cpf.charAt(10));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidCNPJ(String cnpj) {
        if (cnpj.length() != 14 || cnpj.chars().distinct().count() == 1) return false;

        try {
            int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

            int soma = 0;
            for (int i = 0; i < 12; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesos1[i];
            }

            int d1 = soma % 11;
            d1 = (d1 < 2) ? 0 : 11 - d1;

            soma = 0;
            for (int i = 0; i < 13; i++) {
                int dig = Character.getNumericValue(cnpj.charAt(i));
                soma += dig * pesos2[i];
            }

            int d2 = soma % 11;
            d2 = (d2 < 2) ? 0 : 11 - d2;

            return d1 == Character.getNumericValue(cnpj.charAt(12)) &&
                    d2 == Character.getNumericValue(cnpj.charAt(13));
        } catch (Exception e) {
            return false;
        }
    }

}
