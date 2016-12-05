package dk.trustworks.timemanager.service.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import dk.trustworks.framework.model.Project;
import dk.trustworks.framework.network.Locator;

public class GetProjectCommand extends HystrixCommand<Project> {

    private final String uuid;
    private String jwtToken;
    private String projection;

    public GetProjectCommand(String uuid, String jwtToken) {
        super(HystrixCommandGroupKey.Factory.asKey("Project"));
        this.uuid = uuid;
        this.jwtToken = jwtToken;
        projection = "";
    }

    public GetProjectCommand(String uuid, String projection, String jwtToken) {
        this(uuid, jwtToken);
        this.projection = projection;
    }


    public Project run() throws Exception {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/projects/"+uuid)
                .header("accept", "application/json")
                .header("jwt-token", jwtToken)
                .queryString("projection", projection)
                .asJson();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<Project>() {});
    }
}