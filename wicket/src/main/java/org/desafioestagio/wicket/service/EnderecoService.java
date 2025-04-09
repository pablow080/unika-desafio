package org.desafioestagio.wicket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.wicket.model.Model;
import org.desafioestagio.wicket.model.Endereco;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EnderecoService {

    private static final String BASE_API_URL = "http://localhost:8080/api/clientes/";

    // Criação de um novo endereço
    public static Endereco criarEndereco(Long clienteId, Endereco endereco) throws IOException {
        String url = BASE_API_URL + clienteId + "/enderecos";
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        ObjectMapper mapper = new ObjectMapper();
        String jsonBody = mapper.writeValueAsString(endereco);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        if (con.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
            try (InputStream in = con.getInputStream()) {
                return mapper.readValue(in, Endereco.class);
            }
        } else {
            throw new IOException("Erro ao criar endereço: " + con.getResponseCode());
        }
    }

    // Listar todos os endereços de um cliente
    public static List<Endereco> listarEnderecos(Long clienteId) throws IOException {
        String url = BASE_API_URL + clienteId + "/enderecos";
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");

        if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Erro ao listar endereços: " + con.getResponseCode());
        }

        ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = con.getInputStream()) {
            return mapper.readValue(in, mapper.getTypeFactory().constructCollectionType(List.class, Endereco.class));
        }
    }

    // Buscar um endereço específico por ID
    public static Endereco buscarEndereco(Long clienteId, Long enderecoId) throws IOException {
        String url = BASE_API_URL + clienteId + "/enderecos/" + enderecoId;
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");

        if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Erro ao buscar endereço: " + con.getResponseCode());
        }

        ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = con.getInputStream()) {
            return mapper.readValue(in, Endereco.class);
        }
    }

    // Atualizar um endereço existente
    public static Endereco atualizarEndereco(Long clienteId, Long enderecoId, Endereco endereco) throws IOException {
        String url = BASE_API_URL + clienteId + "/enderecos/" + enderecoId;
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        ObjectMapper mapper = new ObjectMapper();
        String jsonBody = mapper.writeValueAsString(endereco);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (InputStream in = con.getInputStream()) {
                return mapper.readValue(in, Endereco.class);
            }
        } else {
            throw new IOException("Erro ao atualizar endereço: " + con.getResponseCode());
        }
    }

    // Marcar um endereço como principal
    public static void marcarPrincipal(Long clienteId, Long enderecoId) throws IOException {
        String url = BASE_API_URL + clienteId + "/enderecos/" + enderecoId + "/principal";
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("PATCH");
        con.setRequestProperty("Accept", "application/json");

        if (con.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
            throw new IOException("Erro ao marcar endereço como principal: " + con.getResponseCode());
        }
    }
}
