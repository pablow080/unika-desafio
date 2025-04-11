package org.desafioestagio.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;


public class WicketApplication extends WebApplication {
    @Override
    public Class<? extends Page> getHomePage() {
        return ClienteListPage.class;
    }

    @Override
    protected void init() {
        super.init();

        getMarkupSettings().setStripWicketTags(true);
        getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
        getDebugSettings().setDevelopmentUtilitiesEnabled(true);

        // 👇 Desabilita CSP para permitir recursos externos (CDNs)
        getCspSettings().blocking().disabled();
    }
}