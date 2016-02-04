package dk.trustworks.bimanager.client.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import dk.trustworks.bimanager.client.RestClient;
import dk.trustworks.bimanager.dto.Client;

import java.util.List;

public class GetClientsCommand {
    public GetClientsCommand() {
    }

    public List<Client> getClients() {
        RestClient.log.debug("RestClient.getClients");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/clients")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Client>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            RestClient.log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: clients ", e);
        }
    }
}