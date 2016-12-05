package dk.trustworks.clientmanager.service.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import dk.trustworks.framework.model.Work;
import dk.trustworks.framework.network.Locator;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class GetWorkByPeriodAndTaskUUIDCommand extends HystrixCommand<List<Work>> {

    private String jwtToken;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String taskUUID;

    public GetWorkByPeriodAndTaskUUIDCommand(LocalDate periodStart, LocalDate periodEnd, String taskUUID, String jwtToken) {
        super(HystrixCommandGroupKey.Factory.asKey("Work"), 10000);
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.taskUUID = taskUUID;
        System.out.println("GetWorkByYearCommand.GetWorkByYearCommand");
        System.out.println("periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "], jwtToken = [" + jwtToken + "]");
        this.jwtToken = jwtToken;
    }

    public List<Work> run() throws Exception {
        System.out.println("GetWorkByPeriodCommand.run");
        HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(5000);
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice") + "/api/works/search/findByPeriodAndTaskUUID")
                    .header("accept", "application/json")
                    .header("jwt-token", jwtToken)
                    .queryString("periodStart", periodStart.toString("yyyy-MM-dd"))
                    .queryString("periodEnd", periodEnd.toString("yyyy-MM-dd"))
                    .queryString("taskuuid", taskUUID)
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Work>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    @Override
    protected List<Work> getFallback() {
        System.out.println("GetWorkByPeriodCommand.getFallback");
        System.out.println("periodStart = " + periodStart);
        System.out.println("periodEnd = " + periodEnd);
        return new ArrayList<>();
    }
}