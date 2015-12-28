package dk.trustworks.timemanager.handlers;

import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.timemanager.service.TaskWeekViewService;

/**
 * Created by hans on 15/05/15.
 */
public class TaskWeekViewHandler extends DefaultHandler {

    private final TaskWeekViewService taskWeekViewService;

    public TaskWeekViewHandler() {
        super("taskweekview");
        this.taskWeekViewService = new TaskWeekViewService();
    }

    @Override
    protected DefaultLocalService getService() {
        return taskWeekViewService;
    }
}