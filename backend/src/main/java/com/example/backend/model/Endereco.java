package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "endereco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String logradouro;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private String cep;

    @Column(nullable = false)
    private String bairro;

    private String telefone;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private Boolean principal = false;

    private String complemento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private com.example.backend.model.Cliente cliente;

    public Boolean isEnderecoPrincipal() {
        return this.principal;
    }

    public void setEnderecoPrincipal(Boolean enderecoPrincipal) {
        this.principal = enderecoPrincipal;
    }
}