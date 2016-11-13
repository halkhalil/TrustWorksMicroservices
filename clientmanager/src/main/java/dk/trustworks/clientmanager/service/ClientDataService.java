package dk.trustworks.clientmanager.service;

import dk.trustworks.clientmanager.model.ClientData;
import dk.trustworks.clientmanager.persistence.ClientDataRepository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by hans on 17/03/15.
 */
public class ClientDataService {

    private ClientDataRepository clientDataRepository;

    public ClientDataService(DataSource ds) {
        clientDataRepository = new ClientDataRepository(ds);
    }

    public List<ClientData> findAll() {
        return clientDataRepository.findAll();
    }

    public ClientData findByUUID(String uuid) {
        return clientDataRepository.findByUUID(uuid);
    }

    public List<ClientData> findByClientUUID(String clientUUID) {
        return clientDataRepository.findByClientUUID(clientUUID);
    }

    public ClientData findByProjectUUID(String projectUUID) {
        return clientDataRepository.findByProjectUUID(projectUUID);
    }

    public void create(ClientData clientData) throws SQLException {
        clientDataRepository.create(clientData);
    }

    public void update(ClientData clientData, String uuid) throws SQLException {
        clientDataRepository.update(clientData, uuid);
    }
}
