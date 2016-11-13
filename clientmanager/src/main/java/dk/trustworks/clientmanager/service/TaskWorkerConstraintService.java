package dk.trustworks.clientmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.clientmanager.model.Client;
import dk.trustworks.clientmanager.model.Project;
import dk.trustworks.clientmanager.model.Task;
import dk.trustworks.clientmanager.model.TaskWorkerConstraint;
import dk.trustworks.clientmanager.persistence.TaskWorkerConstraintRepository;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 17/03/15.
 */
public class TaskWorkerConstraintService {

    private TaskWorkerConstraintRepository taskWorkerConstraintRepository;

    public TaskWorkerConstraintService(DataSource ds) {
        taskWorkerConstraintRepository = new TaskWorkerConstraintRepository(ds);
    }

    public List<TaskWorkerConstraint> findAll() {
        return taskWorkerConstraintRepository.findAll();
    }

    public TaskWorkerConstraint findByUUID(String uuid) {
        return taskWorkerConstraintRepository.findByUUID(uuid);
    }

    public List<TaskWorkerConstraint> findAllByTaskUUIDs(List<Task> tasks) {
        return taskWorkerConstraintRepository.findAllByTaskUUIDs(tasks);
    }

    public List<TaskWorkerConstraint> findByTaskUUID(String taskuuid) {
        return taskWorkerConstraintRepository.findByTaskUUID(taskuuid);
    }

    public TaskWorkerConstraint findByTaskUUIDAndUserUUID(String taskuuid, String useruuid) {
        return taskWorkerConstraintRepository.findByTaskUUIDAndUserUUID(taskuuid, useruuid);
    }

    public void create(TaskWorkerConstraint taskWorkerConstraint) throws SQLException {
        taskWorkerConstraintRepository.create(taskWorkerConstraint);
    }

    public void update(TaskWorkerConstraint taskWorkerConstraint, String uuid) throws SQLException {
        taskWorkerConstraintRepository.update(taskWorkerConstraint, uuid);
    }
}
