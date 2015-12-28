package dk.trustworks.clientmanager.handlers;

import dk.trustworks.clientmanager.service.ProjectService;
import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;

/**
 * Created by hans on 16/03/15.
 */
public class ProjectHandler extends DefaultHandler {

    private final ProjectService projectService;

    public ProjectHandler() {
        super("project");
        this.projectService = new ProjectService();
    }

    @Override
    protected DefaultLocalService getService() {
        return projectService;
    }
}
