package dk.trustworks.bimanager.client.commands;

import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import dk.trustworks.bimanager.dto.User;
import dk.trustworks.framework.servlets.MetricsServletContextListener;
import dk.trustworks.framework.network.Locator;

import java.util.List;

import static com.codahale.metrics.MetricRegistry.name;

public class GetUsersCommand extends HystrixCommand<List<User>> {

    public GetUsersCommand() {
        super(HystrixCommandGroupKey.Factory.asKey("Users"));
    }

    public List<User> run() throws Exception {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/users")
                .header("accept", "application/json")
                .asJson();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<User>>() {
        });
    }
}