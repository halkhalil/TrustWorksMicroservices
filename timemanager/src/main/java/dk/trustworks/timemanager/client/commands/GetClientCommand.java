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

public class GetClientCommand extends HystrixCommand<Client> {

    private final String uuid;

    public GetClientCommand(String uuid) {
        super(HystrixCommandGroupKey.Factory.asKey("Client"));
        this.uuid = uuid;
    }

    public Client run() throws Exception {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/clients/"+uuid)
                .header("accept", "application/json")
                .asJson();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<Client>() {});
    }
}