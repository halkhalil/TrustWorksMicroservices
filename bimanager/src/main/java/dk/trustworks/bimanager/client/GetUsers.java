package dk.trustworks.bimanager.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import dk.trustworks.bimanager.dto.User;

import java.util.List;

public class GetUsers {
    public GetUsers() {
    }

    public List<User> getUsers() {
        RestClient.log.debug("RestClient.getUsers");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/users")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<User>>() {
            });
        } catch (Exception e) {
            RestClient.log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: users ", e);
        }
    }
}