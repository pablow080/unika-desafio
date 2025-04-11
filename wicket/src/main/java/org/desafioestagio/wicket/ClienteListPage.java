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
        // use in-memory ou injete conforme preferir
        ClienteService clienteService = new ClienteService();
        List<Cliente> clientes = clienteService.listarTodos();
        ListModel<Cliente> clientesModel = new ListModel<>(clientes);

        add(new ListView<>("listaClientes", clientesModel) {
            @Override
            protected void populateItem(ListItem<Cliente> item) {
                Cliente cliente = item.getModelObject();

                String nomeOuRazao = cliente.getTipoPessoa().name().equals("FISICA")
                        ? cliente.getNome()
                        : cliente.getRazaoSocial();

                Endereco enderecoPrincipal = cliente.getEnderecos().stream()
                        .filter(Endereco::isEnderecoPrincipal)
                        .findFirst()
                        .orElse(null);

                item.add(new Label("tipoPessoa", cliente.getTipoPessoa().name()));
                item.add(new Label("cpfCnpj", cliente.getCpfCnpj()));
                item.add(new Label("nomeRazao", nomeOuRazao));
                item.add(new Label("rgIe", cliente.getRgIe()));
                item.add(new Label("email", cliente.getEmail()));
                item.add(new Label("telefone", enderecoPrincipal != null ? enderecoPrincipal.getTelefone() : "—"));
                item.add(new Label("cidade", enderecoPrincipal != null ? enderecoPrincipal.getCidade() : "—"));
                item.add(new Label("estado", enderecoPrincipal != null ? enderecoPrincipal.getEstado() : "—"));
                item.add(new Label("ativo", cliente.isAtivo() ? "Sim" : "Não"));
                // Aqui você pode adicionar AjaxLink para ações como editar, visualizar, deletar, etc.
            }
        });
    }
}
