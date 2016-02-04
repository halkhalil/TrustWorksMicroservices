package dk.trustworks.bimanager.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import dk.trustworks.bimanager.dto.Expense;
import dk.trustworks.framework.network.Locator;

import java.util.List;

public class GetExpensesByYearCommand extends HystrixCommand<List<Expense>> {

    private int year;

    public GetExpensesByYearCommand(int year) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.year = year;
    }

    public List<Expense> run() throws Exception {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.get(Locator.getInstance().resolveURL("financeservice") + "/api/expenses/search/findByYear")
                .queryString("year", year)
                .header("accept", "application/json")
                .asJson();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Expense>>() {
        });
    }
}