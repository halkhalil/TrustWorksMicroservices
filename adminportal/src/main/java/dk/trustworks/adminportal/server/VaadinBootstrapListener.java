package dk.trustworks.adminportal.server;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;

/**
 * Created by hans on 30/01/16.
 */
public class VaadinBootstrapListener implements BootstrapListener {
    @Override
    public void modifyBootstrapFragment(BootstrapFragmentResponse response) {

    }

    @Override
    public void modifyBootstrapPage(BootstrapPageResponse response) {
        response.getDocument().head().prependElement("meta").attr("name", "viewport").attr("content", "width=device-width");

    }
}
