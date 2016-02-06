package dk.trustworks.bimanager.client.commands;

import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import dk.trustworks.bimanager.dto.Project;
import dk.trustworks.framework.servlets.MetricsServletContextListener;
import dk.trustworks.framework.network.Locator;

import java.util.List;

import static com.codahale.metrics.MetricRegistry.name;

public class GetProjectsAndTasksAndTaskWorkerConstraintsCommand extends HystrixCommand<List<Project>> {

    //private final Timer responses = MetricsServletContextListener.metricRegistry.timer(name(GetProjectsAndTasksAndTaskWorkerConstraintsCommand.class, "requests"));

    public GetProjectsAndTasksAndTaskWorkerConstraintsCommand() {
        super(HystrixCommandGroupKey.Factory.asKey("Projects"));
    }

    public List<Project> run() throws Exception {
        //final Timer.Context context = responses.time();
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/projects")
                    .queryString("children", "taskuuid/taskworkerconstraintuuid")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Project>>() {
            });
        } finally {
            //context.stop();
        }
    }
}