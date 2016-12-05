package dk.trustworks.timemanager.service;


import dk.trustworks.framework.model.Client;
import dk.trustworks.framework.security.JwtModule;
import dk.trustworks.timemanager.service.commands.GetClientsCommand;

import java.util.List;

/**
 * Created by hans on 24/04/15.
 */
public class ClientService {

    public ClientService() {
    }

    public List<Client> findAll(String projection) {
        return new GetClientsCommand(projection, JwtModule.JWTTOKEN.get()).execute();
    }
}
