package org.desafioestagio.backend.repository;

import org.desafioestagio.backend.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByCpfCnpj(String cpfCnpj);

    boolean existsByEmail(String email);

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Cliente> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);

    Optional<Cliente> findByCpfCnpj(String cpfCnpj);
}