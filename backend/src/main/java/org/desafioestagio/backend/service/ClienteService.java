package org.desafioestagio.backend.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.desafioestagio.backend.dto.ClienteDTO;
import org.desafioestagio.backend.dto.EnderecoDTO;
import org.desafioestagio.backend.exception.ClienteExistenteException;
import org.desafioestagio.backend.exception.ClienteNaoEncontradoException;
import org.desafioestagio.backend.model.Cliente;
import org.desafioestagio.backend.model.Endereco;
import org.desafioestagio.backend.model.TipoPessoa;
import org.desafioestagio.backend.repository.ClienteRepository;
import org.desafioestagio.backend.repository.EnderecoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, EnderecoRepository enderecoRepository) {
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
    }

    @Transactional(readOnly = true)
    public Page<ClienteDTO> listarTodosDTO(Pageable pageable) {
        return clienteRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<ClienteDTO> buscarPorNomeDTO(String nome, Pageable pageable) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public ClienteDTO buscarDTOPorId(Long id) {
        return clienteRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<ClienteDTO> buscarPorCpfCnpjDTO(String cpfCnpj) {
        return clienteRepository.findByCpfCnpj(cpfCnpj).map(this::convertToDTO);
    }

    @Transactional
    public ClienteDTO salvar(@Valid ClienteDTO dto) {
        validarDadosUnicos(dto, null);
        Cliente cliente = convertToEntity(dto);
        Cliente clienteSalvo = clienteRepository.save(cliente);
        return convertToDTO(clienteSalvo);
    }

    @Transactional
    public ClienteDTO atualizar(Long id, @Valid ClienteDTO dto) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com ID: " + id));

        validarDadosUnicos(dto, id);

        // Atualiza campos principais
        clienteExistente.setTipoPessoa(String.valueOf(dto.getTipoPessoa()));
        clienteExistente.setCpfCnpj(dto.getCpfCnpj());
        clienteExistente.setNome(dto.getNome());
        clienteExistente.setRazaoSocial(dto.getRazaoSocial());
        clienteExistente.setInscricaoEstadual(dto.getInscricaoEstadual());
        clienteExistente.setDataNascimento(dto.getDataNascimento());
        clienteExistente.setDataCriacao(dto.getDataCriacao());
        clienteExistente.setEmail(dto.getEmail());
        clienteExistente.setAtivo(dto.isAtivo());

        // Limpa e redefine endereços
        clienteExistente.getEnderecos().clear();
        List<Endereco> enderecos = dto.getEnderecos().stream()
                .map(end -> {
                    Endereco endereco = new Endereco();
                    endereco.setLogradouro(end.getLogradouro());
                    endereco.setNumero(end.getNumero());
                    endereco.setCep(end.getCep());
                    endereco.setBairro(end.getBairro());
                    endereco.setTelefone(end.getTelefone());
                    endereco.setCidade(end.getCidade());
                    endereco.setEstado(end.getEstado());
                    endereco.setComplemento(end.getComplemento());
                    endereco.setEnderecoPrincipal(end.isEnderecoPrincipal());
                    endereco.setCliente(clienteExistente);
                    return endereco;
                })
                .toList();

        clienteExistente.getEnderecos().addAll(enderecos);

        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);
        return convertToDTO(clienteAtualizado);
    }


    @Transactional
    public void excluir(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com ID: " + id));
        enderecoRepository.deleteAll(cliente.getEnderecos());
        clienteRepository.delete(cliente);
    }

    private void validarDadosUnicos(ClienteDTO dto, Long idAtual) {
        Optional<Cliente> existenteCpfCnpj = clienteRepository.findByCpfCnpj(dto.getCpfCnpj());
        if (existenteCpfCnpj.isPresent() && (idAtual == null || !existenteCpfCnpj.get().getId().equals(idAtual))) {
            throw new ClienteExistenteException("Já existe um cliente com esse CPF/CNPJ.");
        }

        if (dto.getEmail() != null) {
            Optional<Cliente> existenteEmail = clienteRepository.findAll().stream()
                    .filter(c -> c.getEmail() != null && c.getEmail().equalsIgnoreCase(dto.getEmail()))
                    .filter(c -> !c.getId().equals(idAtual))
                    .findAny();
            if (existenteEmail.isPresent()) {
                throw new ClienteExistenteException("Já existe um cliente com esse e-mail.");
            }
        }
    }

    public ByteArrayOutputStream exportarClientesParaPdf(String nome, TipoPessoa tipoPessoa, Boolean ativo) {
        List<Cliente> clientes = filtrarClientes(nome, tipoPessoa, ativo);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Document document = new Document()) {

            PdfWriter.getInstance(document, baos);
            document.open();

            // Fontes
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

            // Título
            Paragraph title = new Paragraph("RELATÓRIO DE CLIENTES", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Cria tabela com 5 colunas
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Configura larguras das colunas
            float[] columnWidths = {1f, 2f, 4f, 3f, 2f};
            table.setWidths(columnWidths);

            // Cabeçalho
            addTableHeader(table, "ID", headerFont);
            addTableHeader(table, "TIPO", headerFont);
            addTableHeader(table, "NOME/RAZÃO SOCIAL", headerFont);
            addTableHeader(table, "CPF/CNPJ", headerFont);
            addTableHeader(table, "STATUS", headerFont);

            // Dados
            for (Cliente cliente : clientes) {
                addTableCell(table, cliente.getId().toString(), cellFont);
                addTableCell(table, formatTipoPessoa(cliente.getTipoPessoa()), cellFont);
                addTableCell(table, getNomeOuRazaoSocial(cliente), cellFont);
                addTableCell(table, formatCpfCnpj(cliente.getCpfCnpj()), cellFont);
                addTableCell(table, cliente.getAtivo() ? "Ativo" : "Inativo", cellFont);
            }

            document.add(table);

            // Rodapé
            Paragraph footer = new Paragraph(
                    String.format("Total de clientes: %d\nData do relatório: %s",
                            clientes.size(),
                            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
                    cellFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();
            return baos;
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }

    private List<Cliente> filtrarClientes(String nome, TipoPessoa tipoPessoa, Boolean ativo) {
        Specification<Cliente> spec = Specification.where(null);

        if (nome != null && !nome.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
        }

        if (tipoPessoa != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("tipoPessoa"), tipoPessoa.name()));
        }

        if (ativo != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("ativo"), ativo));
        }

        return clienteRepository.findAll(spec, Sort.by("nome"));
    }

    // Métodos auxiliares
    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }

    private String formatCpfCnpj(String cpfCnpj) {
        if (cpfCnpj == null) return "";
        if (cpfCnpj.length() == 11) { // CPF
            return cpfCnpj.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        } else if (cpfCnpj.length() == 14) { // CNPJ
            return cpfCnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        }
        return cpfCnpj;
    }

    private String formatTipoPessoa(String tipo) {
        if ("FISICA".equalsIgnoreCase(tipo)) return "FÍSICA";
        if ("JURIDICA".equalsIgnoreCase(tipo)) return "JURÍDICA";
        return tipo;
    }

    private String getNomeOuRazaoSocial(Cliente cliente) {
        return "FISICA".equalsIgnoreCase(cliente.getTipoPessoa())
                ? cliente.getNome()
                : cliente.getRazaoSocial();
    }

    public ByteArrayOutputStream exportarClientesParaExcel(String nome, TipoPessoa tipoPessoa, Boolean ativo) {
        List<ClienteDTO> clientes = buscarClientesFiltrados(nome, tipoPessoa);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Clientes");

            // Cabeçalho
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Nome/Razão Social");
            headerRow.createCell(2).setCellValue("CPF/CNPJ");
            headerRow.createCell(3).setCellValue("Tipo");
            headerRow.createCell(4).setCellValue("Ativo");

            // Dados
            int rowNum = 1;
            for (ClienteDTO cliente : clientes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(cliente.getId());
                row.createCell(1).setCellValue(cliente.getTipoPessoa() == TipoPessoa.FISICA ?
                        cliente.getNome() : cliente.getRazaoSocial());
                row.createCell(2).setCellValue(cliente.getCpfCnpj());
                row.createCell(3).setCellValue(cliente.getTipoPessoa().toString());
                row.createCell(4).setCellValue(cliente.isAtivo() ? "Sim" : "Não");
            }

            workbook.write(baos);
            return baos;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel", e);
        }
    }

    private List<ClienteDTO> buscarClientesFiltrados(String nome, TipoPessoa tipoPessoa) {
        return getClienteDTOS(nome, tipoPessoa);
    }

    private List<ClienteDTO> getClienteDTOS(String nome, TipoPessoa tipoPessoa) {
        Specification<Cliente> spec = Specification.where(null);

        if (nome != null && !nome.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
        }

        if (tipoPessoa != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("tipoPessoa"), tipoPessoa.name()));
        }

        return clienteRepository.findAll(spec, Sort.by("nome")).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ClienteDTO> listarFiltrados(String nome, TipoPessoa tipoPessoa) {
        return getClienteDTOS(nome, tipoPessoa);
    }

    private Cliente convertToEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setId(dto.getId());
        cliente.setTipoPessoa(String.valueOf(dto.getTipoPessoa()));
        cliente.setCpfCnpj(dto.getCpfCnpj());
        cliente.setNome(dto.getNome());
        cliente.setRg(dto.getRg());
        cliente.setDataNascimento(dto.getDataNascimento());
        cliente.setRazaoSocial(dto.getRazaoSocial());
        cliente.setInscricaoEstadual(dto.getInscricaoEstadual());
        cliente.setDataCriacao(dto.getDataCriacao());
        cliente.setEmail(dto.getEmail());
        cliente.setAtivo(dto.isAtivo());
        return cliente;
    }

    private ClienteDTO convertToDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setTipoPessoa(TipoPessoa.valueOf(cliente.getTipoPessoa()));
        dto.setCpfCnpj(cliente.getCpfCnpj());
        dto.setNome(cliente.getNome());
        dto.setRg(cliente.getRg());
        dto.setDataNascimento(cliente.getDataNascimento());
        dto.setRazaoSocial(cliente.getRazaoSocial());
        dto.setInscricaoEstadual(cliente.getInscricaoEstadual());
        dto.setDataCriacao(cliente.getDataCriacao());
        dto.setEmail(cliente.getEmail());
        dto.setAtivo(cliente.getAtivo());

        // Se cliente tiver endereços
        if (cliente.getEnderecos() != null) {
            dto.setEnderecos(cliente.getEnderecos().stream()
                    .map(endereco -> {
                        EnderecoDTO enderecoDTO = new EnderecoDTO();
                        enderecoDTO.setId(endereco.getId());
                        enderecoDTO.setLogradouro(endereco.getLogradouro());
                        enderecoDTO.setNumero(endereco.getNumero());
                        enderecoDTO.setCep(endereco.getCep());
                        enderecoDTO.setBairro(endereco.getBairro());
                        enderecoDTO.setTelefone(endereco.getTelefone());
                        enderecoDTO.setCidade(endereco.getCidade());
                        enderecoDTO.setEstado(endereco.getEstado());
                        enderecoDTO.setEnderecoPrincipal(endereco.isPrincipal());
                        enderecoDTO.setComplemento(endereco.getComplemento());

                        return enderecoDTO;
                    })
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
