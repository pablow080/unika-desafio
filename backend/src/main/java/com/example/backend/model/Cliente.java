package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPessoa tipoPessoa;

    @Column(unique = true)
    private String cpfCnpj;

    private String nome; // Para PF

    private String rg; // Para PF

    private LocalDate dataNascimento; // Para PF

    private String razaoSocial; // Para PJ

    private String inscricaoEstadual; // Para PJ

    private LocalDate dataCriacao; // Para PJ

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Endereco> enderecos = new ArrayList<>();

    public enum TipoPessoa {
        FISICA, JURIDICA
    }
}