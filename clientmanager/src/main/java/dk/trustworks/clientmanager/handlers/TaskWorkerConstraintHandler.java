package dk.trustworks.clientmanager.handlers;

import dk.trustworks.clientmanager.service.TaskWorkerConstraintService;
import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;

/**
 * Created by hans on 16/03/15.
 */
public class TaskWorkerConstraintHandler extends DefaultHandler {

    private final TaskWorkerConstraintService taskWorkerConstraintService;

    public TaskWorkerConstraintHandler() {
        super("taskworkerconstraint");
        this.taskWorkerConstraintService = new TaskWorkerConstraintService();
    }

    @Override
    protected DefaultLocalService getService() {
        return taskWorkerConstraintService;
    }
}
