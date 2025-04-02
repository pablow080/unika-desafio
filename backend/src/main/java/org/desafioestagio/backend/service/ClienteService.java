package org.desafioestagio.backend.service;

import jakarta.validation.Valid;
import org.desafioestagio.backend.dto.ClienteDTO;
import org.desafioestagio.backend.exception.ClienteExistenteException;
import org.desafioestagio.backend.exception.ClienteNaoEncontradoException;
import org.desafioestagio.backend.model.Cliente;
import org.desafioestagio.backend.model.Endereco;
import org.desafioestagio.backend.model.TipoPessoa;
import org.desafioestagio.backend.repository.ClienteRepository;
import org.desafioestagio.backend.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, EnderecoRepository enderecoRepository) {
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
    }

    @Transactional(readOnly = true)
    public Page<ClienteDTO> listarTodosDTO(Pageable pageable) {
        Page<Cliente> clientes = clienteRepository.findAll(pageable);
        List<ClienteDTO> clientesDTO = clientes.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(clientesDTO, pageable, clientes.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ClienteDTO buscarDTOPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com ID: " + id));
        return convertToDTO(cliente);
    }

    @Transactional
    public ClienteDTO salvar(@Valid ClienteDTO dto) {
        Cliente cliente = convertToEntity(dto);
        validarCliente(cliente);

        if (clienteRepository.existsByCpfCnpj(cliente.getCpfCnpj())) {
            throw new ClienteExistenteException("Já existe um cliente com este CPF/CNPJ");
        }

        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new ClienteExistenteException("Já existe um cliente com este e-mail");
        }

        Cliente clienteSalvo = clienteRepository.save(cliente);
        return convertToDTO(clienteSalvo);
    }

    @Transactional
    public ClienteDTO atualizar(Long id, @Valid ClienteDTO dto) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com ID: " + id));

        Cliente clienteAtualizado = convertToEntity(dto);
        validarCliente(clienteAtualizado);

        if (!clienteExistente.getCpfCnpj().equals(clienteAtualizado.getCpfCnpj()) &&
                clienteRepository.existsByCpfCnpj(clienteAtualizado.getCpfCnpj())) {
            throw new ClienteExistenteException("Já existe outro cliente com este CPF/CNPJ");
        }

        if (!clienteExistente.getEmail().equals(clienteAtualizado.getEmail()) &&
                clienteRepository.existsByEmail(clienteAtualizado.getEmail())) {
            throw new ClienteExistenteException("Já existe outro cliente com este e-mail");
        }

        // Atualiza os campos
        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setEmail(clienteAtualizado.getEmail());
        clienteExistente.setTipoPessoa(clienteAtualizado.getTipoPessoa());
        // ... outros campos

        Cliente clienteSalvo = clienteRepository.save(clienteExistente);
        return convertToDTO(clienteSalvo);
    }

    @Transactional
    public void excluir(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com ID: " + id));
        clienteRepository.delete(cliente);
    }

    @Transactional
    public Endereco adicionarEndereco(Long clienteId, Endereco endereco) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com ID: " + clienteId));

        if (cliente.getEnderecos() == null) {
            cliente.setEnderecos(new ArrayList<>());
        }

        endereco.setCliente(cliente);

        if (cliente.getEnderecos().isEmpty()) {
            endereco.setPrincipal(true);
        }

        return enderecoRepository.save(endereco);
    }

    @Transactional
    public void definirEnderecoPrincipal(Long clienteId, Long enderecoId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com ID: " + clienteId));

        Endereco novoPrincipal = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        if (!novoPrincipal.getCliente().getId().equals(clienteId)) {
            throw new RuntimeException("Endereço não pertence ao cliente");
        }

        cliente.getEnderecos().forEach(e -> e.setPrincipal(false));
        novoPrincipal.setPrincipal(true);
        enderecoRepository.save(novoPrincipal);
    }

    private void validarCliente(Cliente cliente) {
        if (cliente.getTipoPessoa() == null) {
            throw new IllegalArgumentException("Tipo de pessoa é obrigatório");
        }
        if (cliente.getTipoPessoa() == TipoPessoa.FISICA) {
            if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
                throw new IllegalArgumentException("Nome é obrigatório para pessoa física");
            }
        } else {
            if (cliente.getRazaoSocial() == null || cliente.getRazaoSocial().trim().isEmpty()) {
                throw new IllegalArgumentException("Razão social é obrigatória para pessoa jurídica");
            }
        }
        if (cliente.getCpfCnpj() == null || cliente.getCpfCnpj().trim().isEmpty()) {
            throw new IllegalArgumentException("CPF/CNPJ é obrigatório");
        }
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("E-mail é obrigatório");
        }
    }

    private ClienteDTO convertToDTO(Cliente cliente) {
        // Implemente a conversão de Cliente para ClienteDTO
        return new ClienteDTO(); // Substitua pelo seu mapeamento real
    }

    private Cliente convertToEntity(ClienteDTO dto) {
        // Implemente a conversão de ClienteDTO para Cliente
        return new Cliente(); // Substitua pelo seu mapeamento real
    }
}