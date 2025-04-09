package org.desafioestagio.wicket;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.markup.html.link.Link;

import java.time.LocalDate;
import java.util.List;

public class ClienteFormPage extends WebPage {

    public ClienteFormPage(Long parameters) {
        Cliente cliente = new Cliente();

        Form<Cliente> form = new Form<>("form", new CompoundPropertyModel<>(cliente));
        form.add(new FeedbackPanel("feedback"));

        DropDownChoice<String> tipoPessoa = new DropDownChoice<>(
                "tipoPessoa",
                List.of("FISICA", "JURIDICA")
        );
        tipoPessoa.setRequired(true);
        form.add(tipoPessoa);

        form.add(new TextField<>("cpfCnpj").setRequired(true));
        form.add(new TextField<>("nomeRazao").setRequired(true));
        form.add(new TextField<>("rgIe"));

        TextField<String> emailField = new TextField<>("email");
        emailField.add(EmailAddressValidator.getInstance());
        form.add(emailField);

        form.add(new CheckBox("ativo", Model.of(true)));

        TextField<LocalDate> dataNascimento = new TextField<>("dataNascimento", LocalDate.class);
        form.add(dataNascimento);

        form.add(new Button("salvar") {
            @Override
            public void onSubmit() {
                Cliente c = form.getModelObject();
                System.out.println("Salvar: " + c);
                setResponsePage(ClienteListPage.class);
            }
        });

        form.add(new Link<Void>("cancelar") {
            @Override
            public void onClick() {
                setResponsePage(ClienteListPage.class);
            }
        });

        add(form);
    }

    public static class Cliente {
        private String tipoPessoa;
        private String cpfCnpj;
        private String nomeRazao;
        private String rgIe;
        private String email;
        private boolean ativo = true;
        private LocalDate dataNascimento;

        public String getTipoPessoa() { return tipoPessoa; }
        public void setTipoPessoa(String tipoPessoa) { this.tipoPessoa = tipoPessoa; }
        public String getCpfCnpj() { return cpfCnpj; }
        public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
        public String getNomeRazao() { return nomeRazao; }
        public void setNomeRazao(String nomeRazao) { this.nomeRazao = nomeRazao; }
        public String getRgIe() { return rgIe; }
        public void setRgIe(String rgIe) { this.rgIe = rgIe; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public boolean isAtivo() { return ativo; }
        public void setAtivo(boolean ativo) { this.ativo = ativo; }
        public LocalDate getDataNascimento() { return dataNascimento; }
        public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

        @Override
        public String toString() {
            return "Cliente{" +
                    "tipoPessoa='" + tipoPessoa + '\'' +
                    ", cpfCnpj='" + cpfCnpj + '\'' +
                    ", nomeRazao='" + nomeRazao + '\'' +
                    ", rgIe='" + rgIe + '\'' +
                    ", email='" + email + '\'' +
                    ", ativo=" + ativo +
                    ", dataNascimento=" + dataNascimento +
                    '}';
        }
    }
}
