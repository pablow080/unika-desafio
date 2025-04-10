package com.example.backend.repository;

import com.example.backend.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    List<Endereco> findByClienteId(Long clienteId);

    List<Endereco> findByClienteIdAndPrincipal(Long clienteId, Boolean principal);

    @Modifying
    @Query("DELETE FROM Endereco e WHERE e.cliente.id = :clienteId")
    void deleteByClienteId(@Param("clienteId") Long clienteId);
}