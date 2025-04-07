package org.desafioestagio.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Cliente {

    // Getter and Setter methods
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoPessoa; // FISICA ou JURIDICA
    private String cpfCnpj;
    private String razaoSocial; // se juridica
    private String nome; // se fisica
    private String rg; // se fisica
    private LocalDate dataNascimento; // se fisica
    private String inscricaoEstadual; // se juridica
    private LocalDate dataCriacao; // se juridica
    private String email;
    private boolean ativo;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Endereco> enderecos = new ArrayList<>();

    public boolean getAtivo() {
        return ativo;
    }
}