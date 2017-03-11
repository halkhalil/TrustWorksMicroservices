package dk.trustworks.timemanager.adminportal.server;

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
        response.getDocument().head().prependElement("link").attr("rel", "apple-touch-icon").attr("sizes", "57x57").attr("href","./VAADIN/icons/apple-icon-57x57px.png");
        response.getDocument().head().prependElement("link").attr("rel", "apple-touch-icon").attr("sizes", "72x72").attr("href","./VAADIN/icons/apple-icon-72x72px.png");
        response.getDocument().head().prependElement("link").attr("rel", "apple-touch-icon").attr("sizes", "114x114").attr("href","./VAADIN/icons/apple-icon-114x114px.png");
        response.getDocument().head().prependElement("link").attr("rel", "apple-touch-icon").attr("sizes", "144x144").attr("href","./VAADIN/icons/apple-icon-144x144px.png");
    }
}
