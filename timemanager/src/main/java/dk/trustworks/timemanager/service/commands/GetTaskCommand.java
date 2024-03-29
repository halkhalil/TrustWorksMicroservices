package dk.trustworks.timemanager.service.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import dk.trustworks.framework.model.Task;
import dk.trustworks.framework.network.Locator;

public class GetTaskCommand extends HystrixCommand<Task> {

    private final String uuid;
    private String jwtToken;

    public GetTaskCommand(String uuid, String jwtToken) {
        super(HystrixCommandGroupKey.Factory.asKey("Task"));
        this.uuid = uuid;
        this.jwtToken = jwtToken;
    }

    public Task run() throws Exception {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/tasks/"+uuid)
                .header("accept", "application/json")
                .header("jwt-token", jwtToken)
                .asJson();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<Task>() {});
    }
}