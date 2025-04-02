package org.desafioestagio.backend.controller;

import jakarta.validation.Valid;
import org.desafioestagio.backend.dto.EnderecoDTO;
import org.desafioestagio.backend.service.EnderecoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/clientes/{clienteId}/enderecos")
public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @PostMapping
    public ResponseEntity<EnderecoDTO> criarEndereco(
            @PathVariable Long clienteId,
            @Valid @RequestBody EnderecoDTO enderecoDTO) {

        enderecoDTO.setClienteId(clienteId);
        EnderecoDTO enderecoSalvo = enderecoService.salvar(enderecoDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(enderecoSalvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(enderecoSalvo);
    }

    @GetMapping
    public ResponseEntity<Page<EnderecoDTO>> listarEnderecos(
            @PathVariable Long clienteId,
            Pageable pageable) {

        Page<EnderecoDTO> enderecos = enderecoService.listarPorCliente(clienteId, pageable);
        return ResponseEntity.ok(enderecos);
    }

    @GetMapping("/{enderecoId}")
    public ResponseEntity<EnderecoDTO> buscarEndereco(
            @PathVariable Long clienteId,
            @PathVariable Long enderecoId) {

        EnderecoDTO endereco = enderecoService.buscarPorId(enderecoId);
        return ResponseEntity.ok(endereco);
    }

    @PutMapping("/{enderecoId}")
    public ResponseEntity<EnderecoDTO> atualizarEndereco(
            @PathVariable Long clienteId,
            @PathVariable Long enderecoId,
            @Valid @RequestBody EnderecoDTO enderecoDTO) {

        enderecoDTO.setClienteId(clienteId);
        EnderecoDTO enderecoAtualizado = enderecoService.atualizar(enderecoId, enderecoDTO);
        return ResponseEntity.ok(enderecoAtualizado);
    }

    @DeleteMapping("/{enderecoId}")
    public ResponseEntity<Void> excluirEndereco(
            @PathVariable Long clienteId,
            @PathVariable Long enderecoId) {

        enderecoService.excluir(enderecoId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{enderecoId}/principal")
    public ResponseEntity<Void> definirEnderecoPrincipal(
            @PathVariable Long clienteId,
            @PathVariable Long enderecoId) {

        enderecoService.definirPrincipal(clienteId, enderecoId);
        return ResponseEntity.noContent().build();
    }
}