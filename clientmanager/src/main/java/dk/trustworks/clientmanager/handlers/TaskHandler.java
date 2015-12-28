package dk.trustworks.clientmanager.handlers;

import dk.trustworks.clientmanager.service.TaskService;
import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;

/**
 * Created by hans on 16/03/15.
 */
public class TaskHandler extends DefaultHandler {

    private final TaskService taskService;

    public TaskHandler() {
        super("task");
        this.taskService = new TaskService();
    }

    @Override
    protected DefaultLocalService getService() {
        return taskService;
    }
}
