package dk.trustworks.timemanager.client;

import dk.trustworks.timemanager.client.commands.*;
import dk.trustworks.timemanager.client.dto.*;

import java.util.List;

/**
 * Created by hans on 13/11/2016.
 */
public class RestClient {

    public RestClient() {
    }

    public Client getClient(String uuid) {
        return new GetClientCommand(uuid).execute();
    }

    public Project getProject(String uuid) {
        return new GetProjectCommand(uuid).execute();
    }

    public Task getTask(String uuid) {
        return new GetTaskCommand(uuid).execute();
    }

    public TaskUserPrice getTaskUserPrice(String taskUUID, String userUUID) {
        return new GetTaskUserPriceCommand(userUUID, taskUUID).execute();
    }

    public List<Budget> getBudget(String taskUUID, String userUUID) {
        return new GetBudgetCommand(userUUID, taskUUID).execute();
    }
}
