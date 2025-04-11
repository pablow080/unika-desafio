package org.desafioestagio.wicket.service;

import org.desafioestagio.wicket.model.Cliente;
import org.desafioestagio.wicket.model.TipoPessoa;

import java.util.*;
import java.util.stream.Collectors;

public class ClienteService {

    private final Map<Long, Cliente> clientes = new HashMap<>();
    private long idCounter = 1L;

    public Cliente salvar(Cliente cliente) {
        if (cliente.getId() == null) {
            cliente.setId(idCounter++);
        }
        clientes.put(cliente.getId(), cliente);
        return cliente;
    }

    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        clienteAtualizado.setId(id);
        clientes.put(id, clienteAtualizado);
        return clienteAtualizado;
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return Optional.ofNullable(clientes.get(id));
    }

    public List<Cliente> listarTodos() {
        return new ArrayList<>(clientes.values());
    }

    public void excluir(Long id) {
        clientes.remove(id);
    }

    public void ativarDesativar(Long id, boolean ativo) {
        Cliente cliente = clientes.get(id);
        if (cliente != null) {
            cliente.setAtivo(ativo);
        }
    }
}
