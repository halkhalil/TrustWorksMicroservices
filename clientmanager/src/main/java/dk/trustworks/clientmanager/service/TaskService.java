package dk.trustworks.clientmanager.service;

import dk.trustworks.clientmanager.model.Project;
import dk.trustworks.clientmanager.model.Task;
import dk.trustworks.clientmanager.model.TaskWorkerConstraint;
import dk.trustworks.clientmanager.persistence.TaskRepository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by hans on 17/03/15.
 */
public class TaskService {

    private TaskRepository taskRepository;
    private TaskWorkerConstraintService taskWorkerConstraintService;

    public TaskService(DataSource ds) {
        taskRepository = new TaskRepository(ds);
        taskWorkerConstraintService = new TaskWorkerConstraintService(ds);
    }

    public List<Task> findAll(String projection) {
        List<Task> tasks = taskRepository.findAll();
        if(!projection.contains("taskworkerconstraint")) return tasks;

        return addTaskWorkerConstraintsToTasks(tasks);
    }

    public List<Task> findAllByProjectUUIDs(List<Project> projects, String projection) {
        List<Task> tasks = taskRepository.findAllByProjectUUIDs(projects);
        if(!projection.contains("taskworkerconstraint")) return tasks;

        return addTaskWorkerConstraintsToTasks(tasks);
    }

    public Task findByUUID(String uuid, String projection) {
        Task task = taskRepository.findByUUID(uuid);
        if(!projection.contains("taskworkerconstraint")) return task;

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        return addTaskWorkerConstraintsToTasks(tasks).get(0);
    }

    public List<Task> findByProjectUUID(String projectUUID, String projection) {
        List<Task> tasks = taskRepository.findByProjectUUID(projectUUID);
        if(!projection.contains("taskworkerconstraint")) return tasks;

        return addTaskWorkerConstraintsToTasks(tasks);
    }

    public void create(Task task) throws SQLException {
        taskRepository.create(task);
    }

    public void update(Task task, String uuid) throws SQLException {
        taskRepository.update(task, uuid);
    }

    private ArrayList<Task> addTaskWorkerConstraintsToTasks(List<Task> tasks) {
        Map<String, Task> tasksMap = new HashMap<>();
        for (Task task : tasks) {
            tasksMap.put(task.uuid, task);
        }

        Map<String, TaskWorkerConstraint> taskWorkerConstraintsMap = new HashMap<>();;
        for (TaskWorkerConstraint taskWorkerConstraint : taskWorkerConstraintService.findAllByTaskUUIDs(tasks)) {
            taskWorkerConstraintsMap.put(taskWorkerConstraint.uuid, taskWorkerConstraint);
            tasksMap.get(taskWorkerConstraint.taskuuid).taskworkerconstraints.add(taskWorkerConstraint);
        }

        ArrayList<Task> tasksResult = new ArrayList<>();
        tasksResult.addAll(tasksMap.values());
        return tasksResult;
    }
}
