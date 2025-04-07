package org.desafioestagio.backend.service;

import jakarta.validation.Valid;
import org.desafioestagio.backend.dto.EnderecoDTO;
import org.desafioestagio.backend.exception.ClienteNaoEncontradoException;
import org.desafioestagio.backend.exception.EnderecoNaoEncontradoException;
import org.desafioestagio.backend.model.Cliente;
import org.desafioestagio.backend.model.Endereco;
import org.desafioestagio.backend.repository.ClienteRepository;
import org.desafioestagio.backend.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ClienteRepository clienteRepository;

    @Autowired
    public EnderecoService(EnderecoRepository enderecoRepository, ClienteRepository clienteRepository) {
        this.enderecoRepository = enderecoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public Page<EnderecoDTO> listarTodos(Pageable pageable) {
        Page<Endereco> enderecos = enderecoRepository.findAll(pageable);
        List<EnderecoDTO> enderecosDTO = enderecos.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(enderecosDTO, pageable, enderecos.getTotalElements());
    }

    @Transactional(readOnly = true)
    public EnderecoDTO buscarPorId(Long id) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new EnderecoNaoEncontradoException(id));
        return convertToDTO(endereco);
    }

    @Transactional
    public EnderecoDTO salvar(@Valid EnderecoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ClienteNaoEncontradoException(dto.getClienteId()));

        Endereco endereco = convertToEntity(dto);
        endereco.setCliente(cliente);

        if (cliente.getEnderecos().isEmpty()) {
            endereco.setPrincipal(true);
        }

        if (dto.isEnderecoPrincipal()) {
            desmarcarEnderecosPrincipais(cliente.getId());
            endereco.setPrincipal(true);
        }

        Endereco enderecoSalvo = enderecoRepository.save(endereco);
        return convertToDTO(enderecoSalvo);
    }

    @Transactional
    public EnderecoDTO atualizar(Long id, @Valid EnderecoDTO dto) {
        Endereco enderecoExistente = enderecoRepository.findById(id)
                .orElseThrow(() -> new EnderecoNaoEncontradoException(id));

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ClienteNaoEncontradoException(dto.getClienteId()));

        enderecoExistente.setLogradouro(dto.getLogradouro());
        enderecoExistente.setNumero(dto.getNumero());
        enderecoExistente.setCep(dto.getCep());
        enderecoExistente.setBairro(dto.getBairro());
        enderecoExistente.setTelefone(dto.getTelefone());
        enderecoExistente.setCidade(dto.getCidade());
        enderecoExistente.setEstado(dto.getEstado());
        enderecoExistente.setComplemento(dto.getComplemento());
        enderecoExistente.setCliente(cliente);

        if (dto.isEnderecoPrincipal()) {
            desmarcarEnderecosPrincipais(cliente.getId());
            enderecoExistente.setPrincipal(true);
        } else {
            enderecoExistente.setPrincipal(false);
        }

        Endereco enderecoAtualizado = enderecoRepository.save(enderecoExistente);
        return convertToDTO(enderecoAtualizado);
    }


    private void desmarcarEnderecosPrincipais(Long clienteId) {
        List<Endereco> enderecosDoCliente = enderecoRepository.findByClienteId(clienteId);
        for (Endereco endereco : enderecosDoCliente) {
            if (endereco.isPrincipal()) {
                endereco.setPrincipal(false);
                enderecoRepository.save(endereco);
            }
        }
    }


    private EnderecoDTO convertToDTO(Endereco endereco) {
        EnderecoDTO dto = new EnderecoDTO();
        dto.setId(endereco.getId());
        dto.setLogradouro(endereco.getLogradouro());
        dto.setNumero(endereco.getNumero());
        dto.setCep(endereco.getCep());
        dto.setBairro(endereco.getBairro());
        dto.setTelefone(endereco.getTelefone());
        dto.setCidade(endereco.getCidade());
        dto.setEstado(endereco.getEstado());
        dto.setComplemento(endereco.getComplemento());
        dto.setEnderecoPrincipal(endereco.isPrincipal());
        dto.setClienteId(endereco.getCliente().getId());
        return dto;
    }

    private Endereco convertToEntity(EnderecoDTO dto) {
        Endereco endereco = new Endereco();
        endereco.setId(dto.getId());
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setCep(dto.getCep());
        endereco.setBairro(dto.getBairro());
        endereco.setTelefone(dto.getTelefone());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setComplemento(dto.getComplemento());
        endereco.setPrincipal(dto.isEnderecoPrincipal());
        return endereco;
    }


    @Transactional(readOnly = true)
    public Page<EnderecoDTO> listarPorCliente(Long clienteId, Pageable pageable) {
        // Garante que o cliente existe
        if (!clienteRepository.existsById(clienteId)) {
            throw new ClienteNaoEncontradoException(clienteId);
        }

        // Busca os endereços do cliente com paginação
        Page<Endereco> enderecos = enderecoRepository.findByClienteId(clienteId, pageable);

        // Converte cada entidade para DTO
        List<EnderecoDTO> enderecosDTO = enderecos.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(enderecosDTO, pageable, enderecos.getTotalElements());
    }

    @Transactional
    public void definirPrincipal(Long clienteId, Long enderecoId) {
        // Verifica se o cliente existe
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException(clienteId));

        // Busca o endereço e verifica existência
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new EnderecoNaoEncontradoException(enderecoId));

        // Verifica se o endereço pertence ao cliente
        if (!endereco.getCliente().getId().equals(clienteId)) {
            throw new IllegalArgumentException("Endereço não pertence ao cliente.");
        }

        // Desmarcar todos os endereços do cliente
        enderecoRepository.findByClienteId(clienteId).forEach(e -> {
            if (e.isPrincipal()) {
                e.setPrincipal(false);
                enderecoRepository.save(e);
            }
        });

        // Definir o novo endereço como principal
        endereco.setPrincipal(true);
        enderecoRepository.save(endereco);
    }
}
