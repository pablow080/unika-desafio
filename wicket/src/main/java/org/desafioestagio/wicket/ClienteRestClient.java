package org.desafioestagio.wicket;

import org.desafioestagio.wicket.model.Cliente;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.List;

public class ClienteRestClient {

    private static final String BASE_URL = "http://localhost:8080/api/clientes";
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(10))  // Timeout de 10 segundos para a conexão
            .build();
    private static final ObjectMapper mapper = new ObjectMapper();

    // Método para pegar todos os clientes
    public List<Cliente> getClientes() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Verificando se o código de status é 2xx (OK)
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return mapper.readTree(response.body())
                    .get("content")
                    .traverse(mapper)
                    .readValueAs(new TypeReference<List<Cliente>>() {});
        } else {
            throw new IOException("Falha ao obter clientes. Status Code: " + response.statusCode());
        }
    }

    // Método para adicionar um novo cliente
    public Cliente addCliente(Cliente cliente) throws IOException, InterruptedException {
        String requestBody = mapper.writeValueAsString(cliente);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Verificando se a resposta foi bem-sucedida
        if (response.statusCode() == 201) {
            return mapper.readValue(response.body(), Cliente.class);
        } else {
            throw new IOException("Falha ao adicionar cliente. Status Code: " + response.statusCode());
        }
    }

    // Método para atualizar um cliente existente
    public Cliente updateCliente(Long id, Cliente cliente) throws IOException, InterruptedException {
        String requestBody = mapper.writeValueAsString(cliente);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Verificando se a resposta foi bem-sucedida
        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), Cliente.class);
        } else {
            throw new IOException("Falha ao atualizar cliente. Status Code: " + response.statusCode());
        }
    }

    // Método para excluir um cliente
    public void deleteCliente(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Verificando se a resposta foi bem-sucedida
        if (response.statusCode() != 204) {
            throw new IOException("Falha ao excluir cliente. Status Code: " + response.statusCode());
        }
    }

    // Método para buscar clientes com uma query de pesquisa
    public List<Cliente> searchClientes(String query) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/search?query=" + query))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Verificando se a resposta foi bem-sucedida
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return mapper.readTree(response.body())
                    .traverse(mapper)
                    .readValueAs(new TypeReference<List<Cliente>>() {});
        } else {
            throw new IOException("Falha ao buscar clientes. Status Code: " + response.statusCode());
        }
    }
}
