package org.desafioestagio.wicket;

import org.desafioestagio.wicket.model.Endereco;
import org.desafioestagio.wicket.model.Cliente;
import org.desafioestagio.wicket.service.ClienteService;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.util.ListModel;

import java.util.List;

public class ClienteListPage extends WebPage {

        public ClienteListPage() {
            ClienteService clienteService = new ClienteService();
            List<Cliente> clientes = clienteService.listarTodos();
            ListModel<Cliente> clientesModel = new ListModel<>(clientes);

            add(new ListView<>("listaClientes", clientesModel) {
                @Override
                protected void populateItem(ListItem<Cliente> item) {
                    Cliente cliente = item.getModelObject();
                    boolean isFisica = cliente.getTipoPessoa().name().equals("FISICA");

                    Endereco endereco = cliente.getEnderecos().stream()
                            .filter(Endereco::isEnderecoPrincipal)
                            .findFirst()
                            .orElse(null);

                    String nomeOuRazao = isFisica ? cliente.getNome() : cliente.getRazaoSocial();
                    String identificador = isFisica ? cliente.getRg() : cliente.getInscricaoEstadual();

                    item.add(new Label("tipoPessoa", cliente.getTipoPessoa().name()));
                    item.add(new Label("cpfCnpj", cliente.getCpfCnpj()));
                    item.add(new Label("nomeOuRazao", nomeOuRazao));
                    item.add(new Label("identificador", identificador));
                    item.add(new Label("email", cliente.getEmail()));
                    item.add(new Label("telefone", endereco != null ? endereco.getTelefone() : "—"));
                    item.add(new Label("cidade", endereco != null ? endereco.getCidade() : "—"));
                    item.add(new Label("estado", endereco != null ? endereco.getEstado() : "—"));
                    item.add(new Label("ativo", cliente.isAtivo() ? "Sim" : "Não"));
                // Aqui você pode adicionar AjaxLink para ações como editar, visualizar, deletar, etc.
            }
        });
    }
}
