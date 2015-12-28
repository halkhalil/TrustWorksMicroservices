package dk.trustworks.clientmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.clientmanager.persistence.ClientRepository;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;

import java.sql.SQLException;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 17/03/15.
 */
public class ClientService extends DefaultLocalService {

    private ClientRepository clientRepository;

    public ClientService() {
        clientRepository = new ClientRepository();
    }

    public List<Map<String, Object>> findByActiveTrue(Map<String, Deque<String>> queryParameters) {
        return clientRepository.findByActiveTrue();
    }

    public List<Map<String, Object>> findByActiveTrueOrderByNameAsc(Map<String, Deque<String>> queryParameters) {
        return clientRepository.findByActiveTrue();
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        clientRepository.create(jsonNode);
    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        clientRepository.update(jsonNode, uuid);
    }

    @Override
    public GenericRepository getGenericRepository() {
        return clientRepository;
    }

    @Override
    public String getResourcePath() {
        return "client";
    }
}
