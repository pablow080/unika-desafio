package com.example.backend.util;

import com.example.backend.dto.ClienteDTO;
import com.example.backend.model.Cliente;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class PdfExporter {

    public byte[] exportarClientes(List<ClienteDTO> clientes) throws DocumentException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        // Título
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph titulo = new Paragraph("Relatório de Clientes", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20f);
        document.add(titulo);

        // Tabela
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Cabeçalho da tabela
        String[] headers = {"ID", "Tipo", "CPF/CNPJ", "Nome/Razão Social", "E-mail", "Ativo"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            cell.setPhrase(new Phrase(header));
            table.addCell(cell);
        }

        // Dados da tabela
        for (ClienteDTO cliente : clientes) {
            table.addCell(String.valueOf(cliente.getId()));
            table.addCell(cliente.getTipoPessoa().toString());
            table.addCell(cliente.getCpfCnpj());
            table.addCell(cliente.getTipoPessoa() == Cliente.TipoPessoa.FISICA ?
                    cliente.getNome() : cliente.getRazaoSocial());
            table.addCell(cliente.getEmail());
            table.addCell(cliente.getAtivo() ? "Sim" : "Não");
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    // Método alternativo usando Thymeleaf para templates HTML mais complexos
    public byte[] exportarClientesComTemplate(List<ClienteDTO> clientes) throws DocumentException {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("clientes", clientes);

        String html = templateEngine.process("templates/clientes-pdf", context);

        // Converter HTML para PDF (implementação simplificada)
        // Na prática, você pode usar Flying Saucer ou outro conversor HTML para PDF
        return exportarClientes(clientes); // Usando a implementação simples por enquanto
    }
}