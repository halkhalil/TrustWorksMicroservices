package dk.trustworks.bimanager.client.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import dk.trustworks.bimanager.client.RestClient;
import dk.trustworks.bimanager.dto.Client;
import dk.trustworks.framework.network.Locator;

import java.util.List;

public class GetClientsCommand {
    public GetClientsCommand() {
    }

    public List<Client> getClients() {
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
            throw new RuntimeException("Kunne ikke loade: clients ", e);
        }
    }
}