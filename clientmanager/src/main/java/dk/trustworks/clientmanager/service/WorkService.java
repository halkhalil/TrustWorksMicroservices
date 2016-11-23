package dk.trustworks.clientmanager.service;

import dk.trustworks.clientmanager.model.ProjectBudget;
import dk.trustworks.clientmanager.model.Work;
import dk.trustworks.clientmanager.service.commands.GetWorkByProjectCommand;

import java.util.List;

/**
 * Created by hans on 19/11/2016.
 */
public class WorkService {

    public WorkService() {
    }

    public List<Work> findByProjectUUID(String projectUUID) {
        List<Work> projectBudget = new GetWorkByProjectCommand(projectUUID).execute();
        return projectBudget;
    }
}
