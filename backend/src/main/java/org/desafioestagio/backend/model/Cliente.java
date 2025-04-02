package org.desafioestagio.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPessoa tipoPessoa;

    @Column(unique = true, nullable = false, length = 18)
    private String cpfCnpj;

    @Size(max = 100)
    private String nome;

    @Size(max = 20)
    private String rg;

    private LocalDate dataNascimento;

    @Size(max = 100)
    private String razaoSocial;

    @Size(max = 20)
    private String inscricaoEstadual;

    private LocalDate dataCriacao;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean ativo = true;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Endereco> enderecos = new ArrayList<>();

    // Construtores
    public Cliente() {
    }

    public Cliente(TipoPessoa tipoPessoa, String cpfCnpj, String email) {
        this.tipoPessoa = tipoPessoa;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
    }

    // Métodos utilitários
    public void adicionarEndereco(Endereco endereco) {
        enderecos.add(endereco);
        endereco.setCliente(this);
    }

    public void removerEndereco(Endereco endereco) {
        enderecos.remove(endereco);
        endereco.setCliente(null);
    }

    public Endereco getEnderecoPrincipal() {
        return enderecos.stream()
                .filter(Endereco::isPrincipal)
                .findFirst()
                .orElse(null);
    }

    // equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id) &&
                Objects.equals(cpfCnpj, cliente.cpfCnpj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cpfCnpj);
    }

    // toString
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Cliente{");
        sb.append("id=").append(id);
        sb.append(", tipoPessoa=").append(tipoPessoa);

        if (tipoPessoa == TipoPessoa.FISICA) {
            sb.append(", nome='").append(nome).append('\'');
        } else {
            sb.append(", razaoSocial='").append(razaoSocial).append('\'');
        }

        sb.append(", cpfCnpj='").append(cpfCnpj).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", ativo=").append(ativo);
        sb.append('}');

        return sb.toString();
    }
}
