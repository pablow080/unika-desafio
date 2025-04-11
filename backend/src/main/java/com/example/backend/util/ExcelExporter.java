package com.example.backend.util;

import com.example.backend.dto.ClienteDTO;
import com.example.backend.model.Cliente;
import com.example.backend.repository.EnderecoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelExporter {

    @Autowired
    private EnderecoRepository enderecoRepository;

    public byte[] exportarClientes(List<ClienteDTO> clientes) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Clientes");

            String[] columns = {"ID", "Tipo", "CPF/CNPJ", "Nome/Razão Social", "E-mail", "Telefone", "CEP", "Ativo"};

            // Fontes
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            Font cellFont = workbook.createFont();
            cellFont.setFontHeightInPoints((short) 10);

            // Estilo cabeçalho
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);

            // Estilo células
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFont(cellFont);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setWrapText(true);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);

            // Cabeçalho
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowNum = 1;
            for (ClienteDTO cliente : clientes) {
                Row row = sheet.createRow(rowNum++);
                String[] dados = {
                        String.valueOf(cliente.getId()),
                        cliente.getTipoPessoa().toString(),
                        cliente.getCpfCnpj(),
                        cliente.getTipoPessoa() == Cliente.TipoPessoa.FISICA ? cliente.getNome() : cliente.getRazaoSocial(),
                        cliente.getEmail(),
                        cliente.getEnderecos().isEmpty() ? "" :
                                enderecoRepository.getTelefoneByEnderecoId(cliente.getEnderecos().get(0).getId()),
                        cliente.getEnderecos().isEmpty() ? "" :
                                enderecoRepository.getCepByEnderecoId(cliente.getEnderecos().get(0).getId()),
                        cliente.getAtivo() ? "Sim" : "Não"
                };

                for (int i = 0; i < dados.length; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(dados[i]);
                    cell.setCellStyle(cellStyle);
                }
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}