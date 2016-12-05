package dk.trustworks.usermanager.service;

import dk.trustworks.framework.model.TaskWorkerConstraint;
import dk.trustworks.framework.security.JwtModule;
import dk.trustworks.usermanager.service.commands.GetAllTaskWorkerConstraintCommand;

import java.util.List;

/**
 * Created by hans on 19/11/2016.
 */
public class TaskWorkerConstraintService {

    private static final TaskWorkerConstraintService instance = new TaskWorkerConstraintService();

    private TaskWorkerConstraintService() {
    }

    public static TaskWorkerConstraintService getInstance() {
        return instance;
    }

    public List<TaskWorkerConstraint> findAll() {
        return new GetAllTaskWorkerConstraintCommand(JwtModule.JWTTOKEN.get()).execute();
    }
}
