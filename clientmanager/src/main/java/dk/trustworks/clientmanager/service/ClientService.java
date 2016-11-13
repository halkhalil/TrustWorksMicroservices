package dk.trustworks.clientmanager.service;

import dk.trustworks.clientmanager.model.Client;
import dk.trustworks.clientmanager.model.Project;
import dk.trustworks.clientmanager.persistence.ClientRepository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by hans on 17/03/15.
 */
public class ClientService {

    private ClientRepository clientRepository;
    private ProjectService projectService;

    public ClientService(DataSource ds) {
        clientRepository = new ClientRepository(ds);
        projectService = new ProjectService(ds);
    }

    public List<Client> findAll(String projection) {
        List<Client> clients = clientRepository.findAll();
        if(!projection.contains("project")) return clients;

        return addProjectsToClients(clients, projection);
    }

    public Client findByUUID(String uuid, String projection) {
        Client client = clientRepository.findByUUID(uuid);
        if(!projection.contains("project")) return client;

        List<Client> clients = new ArrayList<>();
        clients.add(client);
        return addProjectsToClients(clients, projection).get(0);
    }

    public List<Client> findByActiveTrue(String projection) {
        List<Client> clients = clientRepository.findByActiveTrue();
        if(!projection.contains("project")) return clients;

        return addProjectsToClients(clients, projection);
    }

    public void create(Client client) throws SQLException {
        clientRepository.create(client);
    }

    public void update(Client client, String uuid) throws SQLException {
        clientRepository.update(client, uuid);
    }

    private ArrayList<Client> addProjectsToClients(List<Client> clients, String projection) {
        Map<String, Client> clientsMap = new HashMap<>();
        for (Client client : clients) {
            clientsMap.put(client.uuid, client);
        }

        Map<String, Project> projectsMap = new HashMap<>();
        for (Project project : projectService.findAllByClientUUIDs(clients, false, projection)) {
            projectsMap.put(project.uuid, project);
            clientsMap.get(project.clientuuid).projects.add(project);
        }

        ArrayList<Client> clientsResult = new ArrayList<>();
        clientsResult.addAll(clientsMap.values());
        return clientsResult;
    }
}
