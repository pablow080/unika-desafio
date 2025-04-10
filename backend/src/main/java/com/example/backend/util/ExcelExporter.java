package com.example.backend.util;

import com.example.backend.dto.ClienteDTO;
import com.example.backend.model.Cliente;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelExporter {

    public byte[] exportarClientes(List<ClienteDTO> clientes) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Clientes");

            // Cabeçalho
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Tipo", "CPF/CNPJ", "Nome/Razão Social", "E-mail", "Ativo"};

            CellStyle headerCellStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerCellStyle.setFont(headerFont);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Dados
            int rowNum = 1;
            for (ClienteDTO cliente : clientes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(cliente.getId());
                row.createCell(1).setCellValue(cliente.getTipoPessoa().toString());
                row.createCell(2).setCellValue(cliente.getCpfCnpj());
                row.createCell(3).setCellValue(
                        cliente.getTipoPessoa() == Cliente.TipoPessoa.FISICA ? cliente.getNome() : cliente.getRazaoSocial());
                row.createCell(4).setCellValue(cliente.getEmail());
                row.createCell(5).setCellValue(cliente.getAtivo() ? "Sim" : "Não");
            }

            // Auto size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}