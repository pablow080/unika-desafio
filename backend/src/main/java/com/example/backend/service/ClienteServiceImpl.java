package com.example.backend.service;

import com.example.backend.dto.ClienteDTO;
import com.example.backend.dto.EnderecoDTO;
import com.example.backend.exception.ClienteException;
import com.example.backend.mapper.ClienteMapper;
import com.example.backend.mapper.EnderecoMapper;
import com.example.backend.model.Cliente;
import com.example.backend.model.Endereco;
import com.example.backend.repository.ClienteRepository;
import com.example.backend.repository.EnderecoRepository;
import com.example.backend.util.ExcelExporter;
import com.example.backend.util.PdfExporter;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final ExcelExporter excelExporter;
    private final PdfExporter pdfExporter;
    private final ClienteMapper clienteMapper;
    private final EnderecoMapper enderecoMapper;

    @Override
    @Transactional
    public ClienteDTO criarCliente(ClienteDTO clienteDTO) {
        // 1. Converte e salva o cliente sem endereços (ainda)
        Cliente cliente = clienteMapper.toEntity(clienteDTO);
        cliente.setEnderecos(null); // para não tentar cascade incorretamente
        cliente = clienteRepository.saveAndFlush(cliente); // força salvar e obter o ID

        // 2. Constrói a lista de endereços com o cliente setado
        if (clienteDTO.getEnderecos() != null) {
            Cliente finalCliente = cliente;
            List<Endereco> enderecos = clienteDTO.getEnderecos().stream()
                    .map(dto -> {
                        Endereco endereco = enderecoMapper.toEntity(dto);
                        endereco.setCliente(finalCliente); // seta o cliente pai
                        return endereco;
                    })
                    .collect(Collectors.toList());

            enderecoRepository.saveAll(enderecos);
            cliente.setEnderecos(enderecos); // agora sim, associa no objeto principal
        }

        return clienteMapper.toDto(cliente);
    }

    @Override
    @Transactional
    public ClienteDTO atualizarCliente(Long id, ClienteDTO clienteDTO) throws ClienteException {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteException("Cliente não encontrado"));

        // Atualiza dados principais do cliente
        cliente.setCpfCnpj(clienteDTO.getCpfCnpj());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTipoPessoa(clienteDTO.getTipoPessoa());
        cliente.setNome(clienteDTO.getNome());
        cliente.setRg(clienteDTO.getRg());
        cliente.setRazaoSocial(clienteDTO.getRazaoSocial());
        cliente.setInscricaoEstadual(clienteDTO.getInscricaoEstadual());
        cliente.setDataNascimento(clienteDTO.getDataNascimento());
        cliente.setAtivo(clienteDTO.getAtivo());

        // Processa endereços
        List<Long> idsDTO = clienteDTO.getEnderecos().stream()
                .map(EnderecoDTO::getId)
                .filter(Objects::nonNull)
                .toList();

        List<Endereco> enderecosExistentes = enderecoRepository.findByClienteId(id);
        for (Endereco endereco : enderecosExistentes) {
            if (!idsDTO.contains(endereco.getId())) {
                enderecoRepository.delete(endereco);
            }
        }

        Cliente finalCliente = cliente;
        List<Endereco> novosEnderecos = clienteDTO.getEnderecos().stream()
                .map(dto -> {
                    Endereco endereco = enderecoMapper.toEntity(dto);
                    endereco.setCliente(finalCliente);
                    return endereco;
                })
                .collect(Collectors.toList());

        cliente.setEnderecos(enderecoRepository.saveAll(novosEnderecos));

        // Limpa os campos inconsistentes conforme o tipo de pessoa
        limparCamposConformeTipoPessoa(cliente);
        cliente = clienteRepository.save(cliente);

        return clienteMapper.toDto(cliente);
    }

    @Override
    public ClienteDTO buscarClientePorId(Long id) throws ClienteException {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteException("Cliente não encontrado com ID: " + id));
        return clienteMapper.toDto(cliente);
    }

    @Override
    public List<ClienteDTO> listarTodosClientes() {
        return clienteRepository.findAll().stream()
                .map(clienteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClienteDTO> listarClientesAtivos() {
        return clienteRepository.findByAtivoTrue().stream()
                .map(clienteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void excluirCliente(Long id) throws ClienteException {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteException("Cliente não encontrado"));

        enderecoRepository.deleteAll(cliente.getEnderecos());
        clienteRepository.delete(cliente);
    }

    @Override
    @Transactional
    public void ativarDesativarCliente(Long id, Boolean ativo) throws ClienteException {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteException("Cliente não encontrado com ID: " + id));
        cliente.setAtivo(ativo);
        clienteRepository.save(cliente);
    }

    @Override
    public byte[] exportarClientesParaExcel() throws ClienteException, IOException {
        List<ClienteDTO> clientes = listarTodosClientes();
        return excelExporter.exportarClientes(clientes);
    }

    @Override
    public byte[] exportarClientesParaPdf() throws ClienteException, DocumentException {
        List<ClienteDTO> clientes = listarTodosClientes();
        return pdfExporter.exportarClientes(clientes);
    }

    private void validarCliente(ClienteDTO clienteDTO) throws ClienteException {
        if (clienteRepository.existsByCpfCnpj(clienteDTO.getCpfCnpj())) {
            throw new ClienteException("Já existe um cliente cadastrado com este CPF/CNPJ");
        }

        if (clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new ClienteException("Já existe um cliente cadastrado com este e-mail");
        }

        validarCamposObrigatorios(clienteDTO);
    }

    private void validarClienteParaAtualizacao(ClienteDTO clienteDTO, Cliente clienteExistente) throws ClienteException {
        if (!clienteExistente.getCpfCnpj().equals(clienteDTO.getCpfCnpj()) &&
                clienteRepository.existsByCpfCnpj(clienteDTO.getCpfCnpj())) {
            throw new ClienteException("Já existe outro cliente cadastrado com este CPF/CNPJ");
        }

        if (!clienteExistente.getEmail().equals(clienteDTO.getEmail()) &&
                clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new ClienteException("Já existe outro cliente cadastrado com este e-mail");
        }

        validarCamposObrigatorios(clienteDTO);
    }

    private void validarCamposObrigatorios(ClienteDTO clienteDTO) throws ClienteException {
        if (clienteDTO.getTipoPessoa() == Cliente.TipoPessoa.FISICA) {
            if (clienteDTO.getNome() == null || clienteDTO.getNome().trim().isEmpty()) {
                throw new ClienteException("Nome é obrigatório para pessoa física");
            }
        } else {
            if (clienteDTO.getRazaoSocial() == null || clienteDTO.getRazaoSocial().trim().isEmpty()) {
                throw new ClienteException("Razão social é obrigatória para pessoa jurídica");
            }
        }
    }

    private void limparCamposConformeTipoPessoa(Cliente cliente) {
        if (cliente.getTipoPessoa() == Cliente.TipoPessoa.FISICA) {
            cliente.setRazaoSocial(null);
            cliente.setInscricaoEstadual(null);
            cliente.setDataCriacao(null);
        } else {
            cliente.setNome(null);
            cliente.setRg(null);
            cliente.setDataNascimento(null);
        }
    }
}
