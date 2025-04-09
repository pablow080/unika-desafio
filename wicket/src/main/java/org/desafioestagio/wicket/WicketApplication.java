package org.desafioestagio.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

import java.util.Locale;

public class WicketApplication extends WebApplication {
    @Override
    public Class<? extends Page> getHomePage() {
        return ClienteListPage.class;
    }
    @Override
    protected void init() {
        super.init();

        // Configurações recomendadas:
        getMarkupSettings().setStripWicketTags(true);
        getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
        // Configure exceções para desenvolvimento
        getDebugSettings().setDevelopmentUtilitiesEnabled(true);
    }
}
