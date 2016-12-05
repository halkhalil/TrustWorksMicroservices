package dk.trustworks.timemanager.client;

import dk.trustworks.framework.model.*;
import dk.trustworks.framework.security.JwtModule;
import dk.trustworks.timemanager.service.commands.*;

import java.util.List;

/**
 * Created by hans on 13/11/2016.
 */
public class RestClient {

    public RestClient() {
    }

    public Client getClient(String uuid) {
        return new GetClientCommand(uuid, JwtModule.JWTTOKEN.get()).execute();
    }

    public Project getProject(String uuid) {
        return new GetProjectCommand(uuid, JwtModule.JWTTOKEN.get()).execute();
    }

    public Task getTask(String uuid) {
        return new GetTaskCommand(uuid, JwtModule.JWTTOKEN.get()).execute();
    }

    public TaskWorkerConstraint getTaskUserPrice(String taskUUID, String userUUID) {
        return new GetTaskUserPriceCommand(userUUID, taskUUID, JwtModule.JWTTOKEN.get()).execute();
    }

    public List<Budget> getBudget(String taskUUID, String userUUID) {
        return new GetBudgetCommand(userUUID, taskUUID, JwtModule.JWTTOKEN.get()).execute();
    }
}
