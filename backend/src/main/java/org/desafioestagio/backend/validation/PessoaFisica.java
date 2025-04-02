package org.desafioestagio.backend.validation;

/**
 * Interface marcadora para validações específicas de Pessoa Física
 *
 * Utilizada em conjunto com a anotação @Validated para validar
 * campos obrigatórios quando o tipo de pessoa for FISICA
 */
public interface PessoaFisica {

    // Pode adicionar constantes úteis se necessário
    public static final int TAMANHO_MAX_CPF = 11;
    public static final int TAMANHO_MAX_RG = 20;
}