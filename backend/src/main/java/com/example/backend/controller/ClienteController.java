package com.example.backend.controller;

import com.example.backend.dto.ClienteDTO;
import com.example.backend.exception.ClienteException;
import com.example.backend.service.ClienteService;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteDTO> criarCliente(@RequestBody ClienteDTO clienteDTO) throws ClienteException {
        ClienteDTO novoCliente = clienteService.criarCliente(clienteDTO);
        return new ResponseEntity<>(novoCliente, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> atualizarCliente(
            @PathVariable Long id, @RequestBody ClienteDTO clienteDTO) throws ClienteException {
        ClienteDTO clienteAtualizado = clienteService.atualizarCliente(id, clienteDTO);
        return ResponseEntity.ok(clienteAtualizado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarClientePorId(@PathVariable Long id) throws ClienteException {
        ClienteDTO cliente = clienteService.buscarClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarTodosClientes() {
        List<ClienteDTO> clientes = clienteService.listarTodosClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<ClienteDTO>> listarClientesAtivos() {
        List<ClienteDTO> clientes = clienteService.listarClientesAtivos();
        return ResponseEntity.ok(clientes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCliente(@PathVariable Long id) throws ClienteException {
        clienteService.excluirCliente(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> ativarDesativarCliente(
            @PathVariable Long id, @RequestParam Boolean ativo) throws ClienteException {
        clienteService.ativarDesativarCliente(id, ativo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exportar-excel")
    public ResponseEntity<byte[]> exportarParaExcel() throws ClienteException, IOException {
        byte[] excelBytes = clienteService.exportarClientesParaExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "clientes.xlsx");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/exportar-pdf")
    public ResponseEntity<byte[]> exportarParaPdf() throws ClienteException, DocumentException {
        byte[] pdfBytes = clienteService.exportarClientesParaPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "clientes.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}