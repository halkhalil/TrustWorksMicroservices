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
import dk.trustworks.timemanager.client.dto.TaskUserPrice;

public class GetTaskUserPriceCommand extends HystrixCommand<TaskUserPrice> {

    private String userUUID;
    private String taskUUID;
    private String jwtToken;

    public GetTaskUserPriceCommand(String userUUID, String taskUUID, String jwtToken) {
        super(HystrixCommandGroupKey.Factory.asKey("Task"));
        this.userUUID = userUUID;
        this.taskUUID = taskUUID;
        this.jwtToken = jwtToken;
    }

    public TaskUserPrice run() throws Exception {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskuserprice/search/findByTaskUUIDAndUserUUID")
                .queryString("useruuid", userUUID)
                .queryString("taskuuid", taskUUID)
                .header("accept", "application/json")
                .header("jwt-token", jwtToken)
                .asJson();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<TaskUserPrice>() {});
    }
}