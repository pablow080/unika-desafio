package com.example.backend.util;

import com.example.backend.dto.ClienteDTO;
import com.example.backend.model.Cliente;
import com.example.backend.repository.EnderecoRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class PdfExporter {

    @Autowired
    private EnderecoRepository enderecoRepository;

    public byte[] exportarClientes(List<ClienteDTO> clientes) throws DocumentException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph titulo = new Paragraph("Relatório de Clientes", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20f);
        document.add(titulo);

        PdfPTable table = new PdfPTable(8); // Corrigido: 8 colunas
        table.setWidthPercentage(110);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        // Larguras proporcionais: ajuste conforme necessário
        table.setWidths(new float[]{1.0f, 1.6f, 2.4f, 3f, 3f, 2.5f, 2f, 1.8f});
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

        BaseColor headerBg = new BaseColor(30, 45, 100); // azul escuro

        String[] headers = {"ID", "Tipo", "CPF/CNPJ", "Nome/Razão Social", "E-mail", "Telefone", "CEP", "Ativo"};

        for (String title : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(title, headerFont));
            headerCell.setBackgroundColor(headerBg);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerCell.setPadding(6);
            table.addCell(headerCell);
        }

        for (ClienteDTO cliente : clientes) {
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

            for (String dado : dados) {
                PdfPCell cell = new PdfPCell(new Phrase(dado, cellFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }
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