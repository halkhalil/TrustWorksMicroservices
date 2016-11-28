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
import dk.trustworks.timemanager.client.dto.User;

import java.util.ArrayList;
import java.util.List;

public class GetUsersCommand extends HystrixCommand<List<User>> {

    private String jwtToken;

    public GetUsersCommand(String jwtToken) {
        super(HystrixCommandGroupKey.Factory.asKey("User"));
        this.jwtToken = jwtToken;
    }

    public List<User> run() throws Exception {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/users")
                .header("accept", "application/json")
                .header("jwt-token", jwtToken)
                .asJson();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<User>>() {});
    }

    @Override
    protected List<User> getFallback() {
        System.out.println("GetUsersCommand.getFallback");
        return new ArrayList<>();
    }
}