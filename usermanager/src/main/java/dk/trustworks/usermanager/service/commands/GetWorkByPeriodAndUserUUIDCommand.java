package dk.trustworks.usermanager.service.commands;

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

public class GetWorkByPeriodAndUserUUIDCommand extends HystrixCommand<List<Work>> {

    private String jwtToken;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String userUUID;

    public GetWorkByPeriodAndUserUUIDCommand(LocalDate periodStart, LocalDate periodEnd, String userUUID, String jwtToken) {
        super(HystrixCommandGroupKey.Factory.asKey("Work"), 10000);
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.userUUID = userUUID;
        System.out.println("GetWorkByPeriodAndUserUUIDCommand.GetWorkByPeriodAndUserUUIDCommand");
        System.out.println("periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "], userUUID = [" + userUUID + "], jwtToken = [" + jwtToken + "]");
        this.jwtToken = jwtToken;
    }

    public List<Work> run() throws Exception {
        System.out.println("GetWorkByPeriodAndUserUUIDCommand.run");
        HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(5000);
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice") + "/api/works/search/findByPeriodAndUserUUID")
                    .header("accept", "application/json")
                    .header("jwt-token", jwtToken)
                    .queryString("periodStart", periodStart.toString("yyyy-MM-dd"))
                    .queryString("periodEnd", periodEnd.toString("yyyy-MM-dd"))
                    .queryString("useruuid", userUUID)
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Work>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    @Override
    protected List<Work> getFallback() {
        System.out.println("GetWorkByPeriodAndUserUUIDCommand.getFallback");
        System.out.println("periodStart = " + periodStart);
        System.out.println("periodEnd = " + periodEnd);
        System.out.println("userUUID = " + userUUID);
        return new ArrayList<>();
    }
}