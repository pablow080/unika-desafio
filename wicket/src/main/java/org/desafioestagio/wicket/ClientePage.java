package org.desafioestagio.wicket;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

public class ClientePage  extends WebPage {
    public ClientePage() {
        add(new Label("welcome", "Welcome to Wicket"));
    }
}
