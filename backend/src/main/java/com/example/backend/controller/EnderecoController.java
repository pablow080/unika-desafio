package com.example.backend.controller;

import com.example.backend.model.Endereco;
import com.example.backend.service.EnderecoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enderecos")
@CrossOrigin(origins = "*") // Permite requisições de qualquer origem (ajuste conforme necessário)
public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @PostMapping
    public ResponseEntity<Endereco> salvar(@RequestBody Endereco endereco) {
        Endereco salvo = enderecoService.salvar(endereco);
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Endereco> atualizar(@PathVariable Long id, @RequestBody Endereco endereco) {
        endereco.setId(id); // garante que estamos atualizando o ID correto
        Endereco atualizado = enderecoService.atualizar(endereco);
        return ResponseEntity.ok(atualizado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Endereco> buscarPorId(@PathVariable Long id) {
        Endereco endereco = enderecoService.buscarPorId(id);
        return ResponseEntity.ok(endereco);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        enderecoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Endereco>> listarTodos() {
        List<Endereco> enderecos = enderecoService.listarTodos();
        return ResponseEntity.ok(enderecos);
    }
}
