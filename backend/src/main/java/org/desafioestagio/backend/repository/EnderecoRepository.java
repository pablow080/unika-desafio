package org.desafioestagio.backend.repository;

import org.desafioestagio.backend.model.Endereco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    Page<Endereco> findByClienteId(Long clienteId, Pageable pageable);
    List<Endereco> findByClienteId(Long clienteId);
    Long cliente_Id(Long clienteId);
}