package org.desafioestagio.wicket.service;

import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import org.desafioestagio.wicket.model.Cliente;
import org.desafioestagio.wicket.model.TipoPessoa;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ClienteService {

    private static final List<Cliente> clientes = new ArrayList<>();
    private static final AtomicLong idSequence = new AtomicLong(1);

    // Listar todos os clientes
    public List<Cliente> listarTodos() {
        return new ArrayList<>(clientes);
    }

    // Criar um novo cliente
    public void criar(Cliente cliente) {
        if (cliente.getId() == null) {
            cliente.setId(idSequence.getAndIncrement());
            clientes.add(cliente);
        }
    }

    // Atualizar um cliente existente
    public void atualizar(Cliente cliente) {
        if (cliente.getId() != null) {
            remover(cliente.getId());
            clientes.add(cliente);
        }
    }

    // Excluir um cliente (marcar como inativo)
    public void excluir(Long id) {
        Cliente cliente = buscarPorId(id);
        if (cliente != null) {
            cliente.setAtivo(false);  // Marca como inativo
        }
    }

    // Exportar para PDF
    public void exportarParaPdf(String fileName) throws DocumentException, FileNotFoundException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        PdfPTable table = new PdfPTable(4);  // Definindo 4 colunas (ajustar conforme necessário)
        table.addCell("ID");
        table.addCell("Nome/Razão Social");
        table.addCell("Tipo Pessoa");
        table.addCell("Email");

        for (Cliente cliente : clientes) {
            table.addCell(cliente.getId().toString());
            table.addCell(cliente.getNomeOuRazao());
            table.addCell(cliente.getTipoPessoa().toString()); // Usando toString() para imprimir o nome do tipoPessoa
            table.addCell(cliente.getEmail());
        }

        document.add(table);
        document.close();
    }

    // Exportar para Excel
    public void exportarParaExcel(String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clientes");

        // Definir cabeçalho
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Nome/Razão Social");
        header.createCell(2).setCellValue("Tipo Pessoa");
        header.createCell(3).setCellValue("Email");

        // Preencher os dados
        int rowNum = 1;
        for (Cliente cliente : clientes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(cliente.getId());
            row.createCell(1).setCellValue(cliente.getNomeOuRazao());
            row.createCell(2).setCellValue(cliente.getTipoPessoa().toString()); // Usando toString() para imprimir o nome do tipoPessoa
            row.createCell(3).setCellValue(cliente.getEmail());
        }

        // Salvar no arquivo
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    // Buscar cliente por ID
    public Cliente buscarPorId(Long id) {
        return clientes.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Remover cliente permanentemente
    public void remover(Long id) {
        clientes.removeIf(c -> c.getId().equals(id));
    }

    // Buscar clientes ativos
    public List<Cliente> buscarAtivos() {
        return clientes.stream()
                .filter(Cliente::isAtivo)
                .collect(Collectors.toList());
    }

    // Buscar clientes por tipo de pessoa
    public List<Cliente> buscarPorTipoPessoa(TipoPessoa tipoPessoa) {
        return clientes.stream()
                .filter(c -> c.getTipoPessoa() == tipoPessoa) // Comparação correta entre TipoPessoa enum
                .collect(Collectors.toList());
    }

    // Buscar clientes por nome ou razão social
    public List<Cliente> buscarPorNomeOuRazao(String nomeOuRazao) {
        return clientes.stream()
                .filter(c -> c.getNomeOuRazao().contains(nomeOuRazao))
                .collect(Collectors.toList());
    }

    // Buscar clientes por CPF/CNPJ
    public List<Cliente> buscarPorCpfCnpj(String cpfCnpj) {
        return clientes.stream()
                .filter(c -> c.getCpfCnpj().contains(cpfCnpj))
                .collect(Collectors.toList());
    }
}
