package org.desafioestagio.backend.service;

import jakarta.validation.Valid;
import org.desafioestagio.backend.dto.EnderecoDTO;
import org.desafioestagio.backend.exception.ClienteNaoEncontradoException;
import org.desafioestagio.backend.exception.EnderecoNaoEncontradoException;
import org.desafioestagio.backend.exception.OperacaoNaoPermitidaException;
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

        Endereco enderecoSalvo = enderecoRepository.save(endereco);
        return convertToDTO(enderecoSalvo);
    }

    @Transactional
    public EnderecoDTO atualizar(Long id, @Valid EnderecoDTO dto) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new EnderecoNaoEncontradoException(id));

        // Verifica se o endereço pertence ao cliente correto
        if (!endereco.getCliente().getId().equals(dto.getClienteId())) {
            throw new OperacaoNaoPermitidaException("Não é permitido alterar o cliente do endereço");
        }

        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setCep(dto.getCep());
        endereco.setPrincipal(dto.isPrincipal());

        Endereco enderecoAtualizado = enderecoRepository.save(endereco);
        return convertToDTO(enderecoAtualizado);
    }

    @Transactional
    public void excluir(Long id) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new EnderecoNaoEncontradoException(id));

        if (endereco.isPrincipal()) {
            throw new OperacaoNaoPermitidaException("Não é permitido excluir o endereço principal");
        }

        enderecoRepository.delete(endereco);
    }

    @Transactional
    public void definirPrincipal(Long clienteId, Long enderecoId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException(clienteId));

        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new EnderecoNaoEncontradoException(enderecoId));

        if (!endereco.getCliente().getId().equals(clienteId)) {
            throw new OperacaoNaoPermitidaException("Endereço não pertence ao cliente informado");
        }

        // Remove a principal atual
        cliente.getEnderecos().forEach(e -> e.setPrincipal(false));

        // Define o novo como principal
        endereco.setPrincipal(true);
        enderecoRepository.save(endereco);
    }

    private EnderecoDTO convertToDTO(Endereco endereco) {
        return new EnderecoDTO(
                endereco.getId(),
                endereco.getCliente().getId(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getCep(),
                endereco.isPrincipal()
        );
    }

    private Endereco convertToEntity(EnderecoDTO dto) {
        Endereco endereco = new Endereco();
        endereco.setId(dto.getId());
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setCep(dto.getCep());
        endereco.setPrincipal(dto.isPrincipal());
        return endereco;
    }

    @Transactional(readOnly = true)
    public Page<EnderecoDTO> listarPorCliente(Long clienteId, Pageable pageable) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ClienteNaoEncontradoException(clienteId);
        }

        Page<Endereco> enderecos = enderecoRepository.findByClienteId(clienteId, pageable);
        List<EnderecoDTO> enderecosDTO = enderecos.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(enderecosDTO, pageable, enderecos.getTotalElements());
    }
}