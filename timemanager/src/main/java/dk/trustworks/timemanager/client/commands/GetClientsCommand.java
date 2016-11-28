package dk.trustworks.timemanager.client.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import dk.trustworks.framework.network.Locator;
import dk.trustworks.timemanager.client.dto.Client;
import dk.trustworks.timemanager.client.dto.Project;

import java.util.List;

public class GetClientsCommand extends HystrixCommand<List<Client>> {

    private String projection;
    private String jwtToken;

    public GetClientsCommand(String jwtToken) {
        super(HystrixCommandGroupKey.Factory.asKey("Project"));
        this.jwtToken = jwtToken;
        projection = "";
    }

    public GetClientsCommand(String projection, String jwtToken) {
        this(jwtToken);
        this.projection = projection;
    }


    public List<Client> run() throws Exception {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/clients")
                .header("accept", "application/json")
                .header("jwt-token", jwtToken)
                .queryString("projection", projection)
                .asJson();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Client>>() {});
    }
}