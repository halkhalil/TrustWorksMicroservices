package dk.trustworks.clientmanager.service;

import dk.trustworks.framework.model.ClientData;
import dk.trustworks.clientmanager.persistence.ClientDataRepository;
import dk.trustworks.framework.security.Authenticator;
import dk.trustworks.framework.security.RoleRight;
import net.sf.cglib.proxy.Enhancer;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by hans on 17/03/15.
 */
public class ClientDataService {

    private ClientDataRepository clientDataRepository;

    public ClientDataService() {
    }

    public ClientDataService(DataSource ds) {
        clientDataRepository = new ClientDataRepository(ds);
    }

    public static ClientDataService getInstance(DataSource ds) {
        ClientDataService service = new ClientDataService(ds);
        return (ClientDataService) Enhancer.create(service.getClass(), new Authenticator(service));
    }

    @RoleRight("tm.user")
    public List<ClientData> findAll() {
        return clientDataRepository.findAll();
    }

    @RoleRight("tm.user")
    public ClientData findByUUID(String uuid) {
        return clientDataRepository.findByUUID(uuid);
    }

    @RoleRight("tm.user")
    public List<ClientData> findByClientUUID(String clientUUID) {
        return clientDataRepository.findByClientUUID(clientUUID);
    }

    @RoleRight("tm.user")
    public ClientData findByProjectUUID(String projectUUID) {
        return clientDataRepository.findByProjectUUID(projectUUID);
    }

    @RoleRight("tm.editor")
    public ClientData create(ClientData clientData) throws SQLException {
        return clientDataRepository.create(clientData);
    }

    @RoleRight("tm.editor")
    public void update(ClientData clientData, String uuid) throws SQLException {
        clientDataRepository.update(clientData, uuid);
    }
}
