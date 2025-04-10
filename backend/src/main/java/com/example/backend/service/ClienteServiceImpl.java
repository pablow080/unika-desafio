package com.example.backend.service;

import com.example.backend.dto.ClienteDTO;
import com.example.backend.dto.EnderecoDTO;
import com.example.backend.exception.ClienteException;
import com.example.backend.mapper.ClienteMapper;
import com.example.backend.mapper.EnderecoMapper;
import com.example.backend.model.Cliente;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final ClienteMapper clienteMapper;
    private final ExcelExporter excelExporter;
    private final PdfExporter pdfExporter;

    @Override
    @Transactional
    public ClienteDTO criarCliente(ClienteDTO clienteDTO) throws ClienteException {
        validarCliente(clienteDTO);

        Cliente cliente = clienteMapper.toEntity(clienteDTO);
        cliente = clienteRepository.save(cliente);

        // Salvar endereços
        if (clienteDTO.getEnderecos() != null) {
            Cliente finalCliente = cliente;
            clienteDTO.getEnderecos().forEach(enderecoDTO -> {
                enderecoDTO.setClienteId(finalCliente.getId());
                enderecoRepository.save(EnderecoMapper.INSTANCE.toEntity(enderecoDTO));
            });
        }

        return clienteMapper.toDto(cliente);
    }

    @Override
    @Transactional
    public ClienteDTO atualizarCliente(Long id, ClienteDTO clienteDTO) throws ClienteException {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteException("Cliente não encontrado com ID: " + id));

        validarClienteParaAtualizacao(clienteDTO, clienteExistente);

        Cliente clienteAtualizado = clienteMapper.toEntity(clienteDTO);
        clienteAtualizado.setId(id);
        clienteAtualizado = clienteRepository.save(clienteAtualizado);

        // Atualizar endereços
        enderecoRepository.deleteByClienteId(id);
        if (clienteDTO.getEnderecos() != null) {
            clienteDTO.getEnderecos().forEach(enderecoDTO -> {
                enderecoDTO.setClienteId(id);
                enderecoRepository.save(EnderecoMapper.INSTANCE.toEntity(enderecoDTO));
            });
        }

        return clienteMapper.toDto(clienteAtualizado);
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
        if (!clienteRepository.existsById(id)) {
            throw new ClienteException("Cliente não encontrado com ID: " + id);
        }
        clienteRepository.deleteById(id);
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
}