package dk.trustworks.bimanager.client.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import dk.trustworks.bimanager.client.RestClient;
import dk.trustworks.bimanager.dto.Project;

import java.util.List;

public class GetProjectsAndTasksAndTaskWorkerConstraintsCommand {
    public GetProjectsAndTasksAndTaskWorkerConstraintsCommand() {
    }

    public List<Project> getProjectsAndTasksAndTaskWorkerConstraints() {
        RestClient.log.debug("RestClient.getProjects");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/projects")
                    .queryString("children", "taskuuid/taskworkerconstraintuuid")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Project>>() {
            });
        } catch (Exception e) {
            RestClient.log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: projects ", e);
        }
    }
}