package dk.trustworks.clientmanager.service;

import dk.trustworks.clientmanager.model.Client;
import dk.trustworks.clientmanager.model.Task;
import dk.trustworks.clientmanager.persistence.ProjectRepository;
import dk.trustworks.clientmanager.model.Project;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 17/03/15.
 */
public class ProjectService {

    private static final Logger logger = LogManager.getLogger();

    private ProjectRepository projectRepository;
    private TaskService taskService;

    public ProjectService(DataSource ds) {
        projectRepository = new ProjectRepository(ds);
        taskService = new TaskService(ds);
    }

    public List<Project> findAll(String projection) {
        List<Project> projects = projectRepository.findAll();
        if(!projection.contains("task")) return projects;

        return addTasksToProjects(projects, projection);
    }

    public List<Project> findAllByClientUUIDs(List<Client> clients, boolean active, String projection) {
        List<Project> projects = projectRepository.findAllByClientUUIDs(clients, active);
        if(!projection.contains("task")) return projects;

        return addTasksToProjects(projects, projection);
    }

    public Project findByUUID(String uuid, String projection) {
        Project project = projectRepository.findByUUID(uuid);
        if(!projection.contains("task")) return project;

        List<Project> projects = new ArrayList<>();
        projects.add(project);
        return addTasksToProjects(projects, projection).get(0);
    }

    public List<Project> findByActiveTrue(String projection) {
        List<Project> projects = projectRepository.findByActiveTrue();
        if(!projection.contains("task")) return projects;

        return addTasksToProjects(projects, projection);
    }

    public List<Project> findByClientUUID(String clientUUID, String projection) {
        List<Project> projects = projectRepository.findByClientUUID(clientUUID);
        if(!projection.contains("task")) return projects;

        return addTasksToProjects(projects, projection);
    }

    public List<Project> findByClientUUIDAndActiveTrue(String clientUUID, String projection) {
        List<Project> projects = projectRepository.findByClientUUIDAndActiveTrue(clientUUID);
        if(!projection.contains("task")) return projects;

        return addTasksToProjects(projects, projection);
    }

    public void create(Project project) throws SQLException {
        logger.debug("ProjectService.create");
        projectRepository.create(project);
    }

    public void update(Project project, String uuid) throws SQLException {
        logger.debug("ProjectService.update");
        projectRepository.update(project, uuid);
    }

    private ArrayList<Project> addTasksToProjects(List<Project> projects, String projection) {
        Map<String, Project> projectsMap = new HashMap<>();
        for (Project project : projects) {
            projectsMap.put(project.uuid, project);
        }

        Map<String, Task> tasksMap = new HashMap<>();;
        for (Task task : taskService.findAllByProjectUUIDs(projects, projection)) {
            tasksMap.put(task.uuid, task);
            projectsMap.get(task.projectuuid).tasks.add(task);
        }

        ArrayList<Project> projectsResult = new ArrayList<>();
        projectsResult.addAll(projectsMap.values());
        return projectsResult;
    }

}
