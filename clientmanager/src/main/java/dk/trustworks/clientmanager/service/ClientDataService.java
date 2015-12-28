package dk.trustworks.clientmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.clientmanager.persistence.ClientDataRepository;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;

import java.sql.SQLException;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 17/03/15.
 */
public class ClientDataService extends DefaultLocalService {

    private ClientDataRepository clientDataRepository;

    public ClientDataService() {
        clientDataRepository = new ClientDataRepository();
    }

    public List<Map<String, Object>> findByActiveTrue(Map<String, Deque<String>> queryParameters) {
        String clientUUID = queryParameters.get("clientuuid").getFirst();
        return clientDataRepository.findByClientUUID(clientUUID);
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        clientDataRepository.create(jsonNode);
    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        clientDataRepository.update(jsonNode, uuid);
    }

    @Override
    public GenericRepository getGenericRepository() {
        return clientDataRepository;
    }

    @Override
    public String getResourcePath() {
        return "clientdata";
    }
}
