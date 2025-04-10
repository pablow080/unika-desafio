package com.example.backend.service;

import com.example.backend.dto.ClienteDTO;
import com.example.backend.exception.ClienteException;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.List;

public interface ClienteService {

    ClienteDTO criarCliente(ClienteDTO clienteDTO) throws ClienteException;

    ClienteDTO atualizarCliente(Long id, ClienteDTO clienteDTO) throws ClienteException;

    ClienteDTO buscarClientePorId(Long id) throws ClienteException;

    List<ClienteDTO> listarTodosClientes();

    List<ClienteDTO> listarClientesAtivos();

    void excluirCliente(Long id) throws ClienteException;

    void ativarDesativarCliente(Long id, Boolean ativo) throws ClienteException;

    byte[] exportarClientesParaExcel() throws ClienteException, IOException;

    byte[] exportarClientesParaPdf() throws ClienteException, DocumentException;
}