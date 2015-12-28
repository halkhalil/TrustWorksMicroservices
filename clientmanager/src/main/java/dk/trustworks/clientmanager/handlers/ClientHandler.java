package dk.trustworks.clientmanager.handlers;

import dk.trustworks.clientmanager.service.ClientService;
import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;

/**
 * Created by hans on 16/03/15.
 */
public class ClientHandler extends DefaultHandler {

    private final ClientService clientService;

    public ClientHandler() {
        super("client");
        this.clientService = new ClientService();
    }

    @Override
    protected DefaultLocalService getService() {
        return clientService;
    }
}
