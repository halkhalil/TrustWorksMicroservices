package dk.trustworks.bimanager.client.commands;

import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import dk.trustworks.bimanager.dto.Client;
import dk.trustworks.framework.servlets.MetricsServletContextListener;
import dk.trustworks.framework.network.Locator;

import java.util.List;

import static com.codahale.metrics.MetricRegistry.name;

public class GetClientsCommand extends HystrixCommand<List<Client>> {

    //private final Timer responses = MetricsServletContextListener.metricRegistry.timer(name(GetClientsCommand.class, "requests"));

    public GetClientsCommand() {
        super(HystrixCommandGroupKey.Factory.asKey("Clients"));
    }

    public List<Client> run() throws Exception {
        //final Timer.Context context = responses.time();
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/clients")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Client>>() {
            });
        } finally {
           // context.stop();
        }
    }
}