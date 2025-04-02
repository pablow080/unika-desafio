package org.desafioestagio.backend.validation;

/**
 * Interface marcadora para validações específicas de Pessoa Jurídica
 *
 * Utilizada em conjunto com a anotação @Validated para validar
 * campos obrigatórios quando o tipo de pessoa for JURIDICA
 */
public interface PessoaJuridica {

    // Constantes úteis para validação
    public static final int TAMANHO_MAX_CNPJ = 14;
    public static final int TAMANHO_MAX_INSCRICAO_ESTADUAL = 20;
    public static final int TAMANHO_MAX_RAZAO_SOCIAL = 100;
}