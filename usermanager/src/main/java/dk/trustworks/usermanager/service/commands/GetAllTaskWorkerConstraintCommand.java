package dk.trustworks.usermanager.service.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import dk.trustworks.framework.model.TaskWorkerConstraint;
import dk.trustworks.framework.network.Locator;

import java.util.List;

public class GetAllTaskWorkerConstraintCommand extends HystrixCommand<List<TaskWorkerConstraint>> {

    private String jwtToken;

    public GetAllTaskWorkerConstraintCommand(String jwtToken) {
        super(HystrixCommandGroupKey.Factory.asKey("Task"));
        this.jwtToken = jwtToken;
    }

    public List<TaskWorkerConstraint> run() throws Exception {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskuserprice")
                .header("accept", "application/json")
                .header("jwt-token", jwtToken)
                .asJson();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<TaskWorkerConstraint>>() {
        });
    }
}