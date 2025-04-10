package com.example.backend.repository;

import com.example.backend.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpfCnpj(String cpfCnpj);

    Optional<Cliente> findByEmail(String email);

    boolean existsByCpfCnpj(String cpfCnpj);

    boolean existsByEmail(String email);

    List<Cliente> findByAtivoTrue();
}