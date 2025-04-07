package org.desafioestagio.backend.service;

import jakarta.validation.Valid;
import org.desafioestagio.backend.dto.ClienteDTO;
import org.desafioestagio.backend.dto.EnderecoDTO;
import org.desafioestagio.backend.exception.ClienteExistenteException;
import org.desafioestagio.backend.exception.ClienteNaoEncontradoException;
import org.desafioestagio.backend.model.Cliente;
import org.desafioestagio.backend.model.Endereco;
import org.desafioestagio.backend.model.TipoPessoa;
import org.desafioestagio.backend.repository.ClienteRepository;
import org.desafioestagio.backend.repository.EnderecoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, EnderecoRepository enderecoRepository) {
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
    }

    @Transactional(readOnly = true)
    public Page<ClienteDTO> listarTodosDTO(Pageable pageable) {
        return clienteRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<ClienteDTO> buscarPorNomeDTO(String nome, Pageable pageable) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public ClienteDTO buscarDTOPorId(Long id) {
        return clienteRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<ClienteDTO> buscarPorCpfCnpjDTO(String cpfCnpj) {
        return clienteRepository.findByCpfCnpj(cpfCnpj).map(this::convertToDTO);
    }

    @Transactional
    public ClienteDTO salvar(@Valid ClienteDTO dto) {
        validarDadosUnicos(dto, null);
        Cliente cliente = convertToEntity(dto);
        Cliente clienteSalvo = clienteRepository.save(cliente);
        return convertToDTO(clienteSalvo);
    }

    @Transactional
    public ClienteDTO atualizar(Long id, @Valid ClienteDTO dto) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com ID: " + id));

        validarDadosUnicos(dto, id);

        // Atualiza campos principais
        clienteExistente.setTipoPessoa(String.valueOf(dto.getTipoPessoa()));
        clienteExistente.setCpfCnpj(dto.getCpfCnpj());
        clienteExistente.setNome(dto.getNome());
        clienteExistente.setRazaoSocial(dto.getRazaoSocial());
        clienteExistente.setInscricaoEstadual(dto.getInscricaoEstadual());
        clienteExistente.setDataNascimento(dto.getDataNascimento());
        clienteExistente.setDataCriacao(dto.getDataCriacao());
        clienteExistente.setEmail(dto.getEmail());
        clienteExistente.setAtivo(dto.isAtivo());

        // Limpa e redefine endereços
        clienteExistente.getEnderecos().clear();
        List<Endereco> enderecos = dto.getEnderecos().stream()
                .map(end -> {
                    Endereco endereco = new Endereco();
                    endereco.setLogradouro(end.getLogradouro());
                    endereco.setNumero(end.getNumero());
                    endereco.setCep(end.getCep());
                    endereco.setBairro(end.getBairro());
                    endereco.setTelefone(end.getTelefone());
                    endereco.setCidade(end.getCidade());
                    endereco.setEstado(end.getEstado());
                    endereco.setComplemento(end.getComplemento());
                    endereco.setEnderecoPrincipal(end.isEnderecoPrincipal());
                    endereco.setCliente(clienteExistente);
                    return endereco;
                })
                .toList();

        clienteExistente.getEnderecos().addAll(enderecos);

        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);
        return convertToDTO(clienteAtualizado);
    }


    @Transactional
    public void excluir(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com ID: " + id));
        enderecoRepository.deleteAll(cliente.getEnderecos());
        clienteRepository.delete(cliente);
    }

    private void validarDadosUnicos(ClienteDTO dto, Long idAtual) {
        Optional<Cliente> existente = clienteRepository.findByCpfCnpj(dto.getCpfCnpj());
        if (existente.isPresent() && (idAtual == null || !existente.get().getId().equals(idAtual))) {
            throw new ClienteExistenteException("Já existe um cliente com esse CPF/CNPJ.");
        }
    }

    private Cliente convertToEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setId(dto.getId());
        cliente.setTipoPessoa(String.valueOf(dto.getTipoPessoa()));
        cliente.setCpfCnpj(dto.getCpfCnpj());
        cliente.setNome(dto.getNome());
        cliente.setRg(dto.getRg());
        cliente.setDataNascimento(dto.getDataNascimento());
        cliente.setRazaoSocial(dto.getRazaoSocial());
        cliente.setInscricaoEstadual(dto.getInscricaoEstadual());
        cliente.setDataCriacao(dto.getDataCriacao());
        cliente.setEmail(dto.getEmail());
        cliente.setAtivo(dto.isAtivo());
        return cliente;
    }

    private ClienteDTO convertToDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setTipoPessoa(TipoPessoa.valueOf(cliente.getTipoPessoa()));
        dto.setCpfCnpj(cliente.getCpfCnpj());
        dto.setNome(cliente.getNome());
        dto.setRg(cliente.getRg());
        dto.setDataNascimento(cliente.getDataNascimento());
        dto.setRazaoSocial(cliente.getRazaoSocial());
        dto.setInscricaoEstadual(cliente.getInscricaoEstadual());
        dto.setDataCriacao(cliente.getDataCriacao());
        dto.setEmail(cliente.getEmail());
        dto.setAtivo(cliente.getAtivo());

        // Se cliente tiver endereços
        if (cliente.getEnderecos() != null) {
            dto.setEnderecos(cliente.getEnderecos().stream()
                    .map(endereco -> {
                        EnderecoDTO enderecoDTO = new EnderecoDTO();
                        enderecoDTO.setId(endereco.getId());
                        enderecoDTO.setLogradouro(endereco.getLogradouro());
                        enderecoDTO.setNumero(endereco.getNumero());
                        enderecoDTO.setCep(endereco.getCep());
                        enderecoDTO.setBairro(endereco.getBairro());
                        enderecoDTO.setTelefone(endereco.getTelefone());
                        enderecoDTO.setCidade(endereco.getCidade());
                        enderecoDTO.setEstado(endereco.getEstado());
                        enderecoDTO.setEnderecoPrincipal(endereco.isPrincipal());
                        enderecoDTO.setComplemento(endereco.getComplemento());

                        return enderecoDTO;
                    })
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
