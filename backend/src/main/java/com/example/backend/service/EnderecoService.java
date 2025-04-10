package com.example.backend.service;

import com.example.backend.model.Endereco;
import com.example.backend.repository.EnderecoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    public Endereco salvar(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    public Endereco atualizar(Endereco enderecoAtualizado) {
        Endereco enderecoExistente = buscarPorId(enderecoAtualizado.getId());

        enderecoExistente.setLogradouro(enderecoAtualizado.getLogradouro());
        enderecoExistente.setNumero(enderecoAtualizado.getNumero());
        enderecoExistente.setCep(enderecoAtualizado.getCep());
        enderecoExistente.setBairro(enderecoAtualizado.getBairro());
        enderecoExistente.setTelefone(enderecoAtualizado.getTelefone());
        enderecoExistente.setCidade(enderecoAtualizado.getCidade());
        enderecoExistente.setEstado(enderecoAtualizado.getEstado());
        enderecoExistente.setEnderecoPrincipal(enderecoAtualizado.isEnderecoPrincipal());
        enderecoExistente.setComplemento(enderecoAtualizado.getComplemento());
        enderecoExistente.setCliente(enderecoAtualizado.getCliente());

        return enderecoRepository.save(enderecoExistente);
    }

    public Endereco buscarPorId(Long id) {
        return enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado com ID: " + id));
    }

    public void deletar(Long id) {
        enderecoRepository.deleteById(id);
    }

    public List<Endereco> listarTodos() {
        return enderecoRepository.findAll();
    }
}
