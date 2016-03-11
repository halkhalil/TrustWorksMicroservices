package dk.trustworks.bimanager.client.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import dk.trustworks.bimanager.dto.Client;
import dk.trustworks.framework.network.Locator;

import java.util.List;

public class GetClientsGraphCommand extends HystrixCommand<List<Client>> {

    //private final Timer responses = MetricsServletContextListener.metricRegistry.timer(name(GetClientsCommand.class, "requests"));

    public GetClientsGraphCommand() {
        super(HystrixCommandGroupKey.Factory.asKey("Clients"));
    }

    public List<Client> run() throws Exception {
        //final Timer.Context context = responses.time();
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/clients")
                    .queryString("children", "projectuuid/taskuuid/taskworkerconstraintuuid")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Client>>() {});
        } finally {
        }
    }
}