package org.desafioestagio.wicket.service;

import org.desafioestagio.wicket.model.Endereco;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class EnderecoService {

    private final Map<Long, Endereco> enderecoMap = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1L);

    public Endereco salvar(Endereco endereco) {
        if (endereco.getId() == null) {
            endereco.setId(idGenerator.getAndIncrement());
        }
        enderecoMap.put(endereco.getId(), endereco);
        return endereco;
    }

    public Endereco atualizar(Endereco enderecoAtualizado) {
        if (enderecoAtualizado.getId() == null || !enderecoMap.containsKey(enderecoAtualizado.getId())) {
            throw new RuntimeException("Endereço não encontrado com ID: " + enderecoAtualizado.getId());
        }

        enderecoMap.put(enderecoAtualizado.getId(), enderecoAtualizado);
        return enderecoAtualizado;
    }

    public Endereco buscarPorId(Long id) {
        Endereco endereco = enderecoMap.get(id);
        if (endereco == null) {
            throw new RuntimeException("Endereço não encontrado com ID: " + id);
        }
        return endereco;
    }

    public void deletar(Long id) {
        enderecoMap.remove(id);
    }

    public List<Endereco> listarTodos() {
        return new ArrayList<>(enderecoMap.values());
    }

    public List<Endereco> listarPorClienteId(Long clienteId) {
        return enderecoMap.values().stream()
                .filter(e -> e.getClienteId() != null && clienteId.equals(e.getClienteId()))
                .collect(Collectors.toList());
    }
}
