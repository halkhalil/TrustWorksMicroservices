package dk.trustworks.clientmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.clientmanager.model.*;
import dk.trustworks.clientmanager.persistence.TaskWorkerConstraintRepository;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by hans on 17/03/15.
 */
public class TaskWorkerConstraintService {

    private TaskWorkerConstraintRepository taskWorkerConstraintRepository;
    private TaskWorkerConstraintBudgetService taskWorkerConstraintBudgetService;

    public TaskWorkerConstraintService(DataSource ds) {
        taskWorkerConstraintRepository = new TaskWorkerConstraintRepository(ds);
        taskWorkerConstraintBudgetService = new TaskWorkerConstraintBudgetService(ds);
    }

    public List<TaskWorkerConstraint> findAll() {
        return taskWorkerConstraintRepository.findAll();
    }

    public TaskWorkerConstraint findByUUID(String uuid) {
        return taskWorkerConstraintRepository.findByUUID(uuid);
    }

    public List<TaskWorkerConstraint> findAllByTaskUUIDs(List<Task> tasks, String projection) {
        List<TaskWorkerConstraint> taskWorkerConstraints = taskWorkerConstraintRepository.findAllByTaskUUIDs(tasks);
        if(!projection.contains("taskworkerconstraintbudget")) return taskWorkerConstraints;

        return addTaskWorkerConstraintBudgetsToTaskWorkerConstraint(taskWorkerConstraints);
    }

    public List<TaskWorkerConstraint> findByTaskUUID(String taskuuid) {
        return taskWorkerConstraintRepository.findByTaskUUID(taskuuid);
    }

    // Verifired
    public TaskWorkerConstraint findByTaskUUIDAndUserUUID(String taskuuid, String useruuid, String projection) {
        TaskWorkerConstraint taskWorkerConstraint = taskWorkerConstraintRepository.findByTaskUUIDAndUserUUID(taskuuid, useruuid);
        if(!projection.contains("taskworkerconstraintbudget")) return taskWorkerConstraint;

        List<TaskWorkerConstraint> taskWorkerConstraints = new ArrayList<>();
        taskWorkerConstraints.add(taskWorkerConstraint);
        return addTaskWorkerConstraintBudgetsToTaskWorkerConstraint(taskWorkerConstraints).get(0);
    }

    public void create(TaskWorkerConstraint taskWorkerConstraint) throws SQLException {
        taskWorkerConstraintRepository.create(taskWorkerConstraint);
    }

    public void update(TaskWorkerConstraint taskWorkerConstraint, String uuid) throws SQLException {
        taskWorkerConstraintRepository.update(taskWorkerConstraint, uuid);
    }

    private List<TaskWorkerConstraint> addTaskWorkerConstraintBudgetsToTaskWorkerConstraint(List<TaskWorkerConstraint> taskWorkerConstraints) {
        Map<String, TaskWorkerConstraint> taskWorkerConstraintsMap = new HashMap<>();
        //Map<String, TaskWorkerConstraintBudget> taskWorkerConstraintBudgetsMap = new HashMap<>();;
        for (TaskWorkerConstraint taskWorkerConstraint : taskWorkerConstraints) {
            taskWorkerConstraintsMap.put(taskWorkerConstraint.uuid, taskWorkerConstraint);

            for (TaskWorkerConstraintBudget taskWorkerConstraintBudget : taskWorkerConstraintBudgetService.findByTaskUUIDAndUserUUID(taskWorkerConstraint.useruuid, taskWorkerConstraint.taskuuid)) {
                //taskWorkerConstraintBudgetsMap.put(taskWorkerConstraintBudget.uuid, taskWorkerConstraintBudget);
                taskWorkerConstraint.taskWorkerConstraintBudgets.add(taskWorkerConstraintBudget);
            }
        }

        ArrayList<TaskWorkerConstraint> taskWorkerConstraintsResult = new ArrayList<>();
        taskWorkerConstraintsResult.addAll(taskWorkerConstraintsMap.values());
        return taskWorkerConstraints;
    }
}
