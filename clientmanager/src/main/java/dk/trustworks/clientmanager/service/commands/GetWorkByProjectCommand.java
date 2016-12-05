package dk.trustworks.clientmanager.service.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import dk.trustworks.framework.model.Work;
import dk.trustworks.framework.network.Locator;

import java.util.ArrayList;
import java.util.List;

public class GetWorkByProjectCommand extends HystrixCommand<List<Work>> {

    private final String projectUUID;
    private String jwtToken;

    public GetWorkByProjectCommand(String projectUUID, String jwtToken) {
        super(HystrixCommandGroupKey.Factory.asKey("Work"));
        this.projectUUID = projectUUID;
        this.jwtToken = jwtToken;
        System.out.println("GetWorkByProjectCommand.GetWorkByProjectCommand");
        System.out.println("projectUUID = [" + projectUUID + "]");
    }

    public List<Work> run() throws Exception {
        System.out.println("GetWorkByProjectCommand.run");
        HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice") + "/api/works/search/findByProjectUUID")
                .header("accept", "application/json")
                .header("jwt-token", jwtToken)
                .queryString("projectuuid", projectUUID)
                .asJson();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Work>>() {});
    }

    @Override
    protected List<Work> getFallback() {
        System.out.println("GetWorkByProjectCommand.getFallback");
        System.out.println("projectUUID = " + projectUUID);
        return new ArrayList<>();
    }
}