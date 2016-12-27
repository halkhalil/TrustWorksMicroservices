package dk.trustworks.clientmanager.service;

import dk.trustworks.clientmanager.persistence.TaskWorkerConstraintRepository;
import dk.trustworks.framework.model.Task;
import dk.trustworks.framework.model.TaskWorkerConstraint;
import dk.trustworks.framework.model.TaskWorkerConstraintBudget;
import dk.trustworks.framework.model.User;
import dk.trustworks.framework.security.Authenticator;
import dk.trustworks.framework.security.RoleRight;
import net.sf.cglib.proxy.Enhancer;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by hans on 17/03/15.
 */
public class TaskWorkerConstraintService {

    private TaskWorkerConstraintRepository taskWorkerConstraintRepository;
    private TaskWorkerConstraintBudgetService taskWorkerConstraintBudgetService;
    private UserService userService;

    public TaskWorkerConstraintService() {
    }

    public TaskWorkerConstraintService(DataSource ds) {
        taskWorkerConstraintRepository = new TaskWorkerConstraintRepository(ds);
        taskWorkerConstraintBudgetService = new TaskWorkerConstraintBudgetService(ds);
        userService = UserService.getInstance();
    }

    public static TaskWorkerConstraintService getInstance(DataSource ds) {
        TaskWorkerConstraintService service = new TaskWorkerConstraintService(ds);
        return (TaskWorkerConstraintService) Enhancer.create(service.getClass(), new Authenticator(service));
    }

    @RoleRight("tm.user")
    public List<TaskWorkerConstraint> findAll(String projection) {
        List<TaskWorkerConstraint> taskWorkerConstraints = taskWorkerConstraintRepository.findAll();
        if(projection.contains("taskworkerconstraintbudget")) taskWorkerConstraints = addTaskWorkerConstraintBudgetsToTaskWorkerConstraint(taskWorkerConstraints);
        if(projection.contains("user")) taskWorkerConstraints = addUsersToTaskWorkerConstraint(taskWorkerConstraints);

        return taskWorkerConstraints;
    }

    @RoleRight("tm.user")
    public TaskWorkerConstraint findByUUID(String uuid) {
        return taskWorkerConstraintRepository.findByUUID(uuid);
    }

    @RoleRight("tm.user")
    public List<TaskWorkerConstraint> findAllByTaskUUIDs(List<Task> tasks, String projection) {
        List<TaskWorkerConstraint> taskWorkerConstraints = taskWorkerConstraintRepository.findAllByTaskUUIDs(tasks);
        if(projection.contains("taskworkerconstraintbudget")) taskWorkerConstraints = addTaskWorkerConstraintBudgetsToTaskWorkerConstraint(taskWorkerConstraints);
        if(projection.contains("user")) taskWorkerConstraints = addUsersToTaskWorkerConstraint(taskWorkerConstraints);

        return taskWorkerConstraints;
    }

    @RoleRight("tm.user")
    public List<TaskWorkerConstraint> findByTaskUUID(String taskuuid, String projection) {
        List<TaskWorkerConstraint> taskWorkerConstraints = taskWorkerConstraintRepository.findByTaskUUID(taskuuid);
        if(projection.contains("taskworkerconstraintbudget")) taskWorkerConstraints = addTaskWorkerConstraintBudgetsToTaskWorkerConstraint(taskWorkerConstraints);
        if(projection.contains("user")) taskWorkerConstraints = addUsersToTaskWorkerConstraint(taskWorkerConstraints);

        return taskWorkerConstraints;
    }

    // Verifired
    @RoleRight("tm.user")
    public TaskWorkerConstraint findByTaskUUIDAndUserUUID(String taskuuid, String useruuid, String projection) {
        TaskWorkerConstraint taskWorkerConstraint = taskWorkerConstraintRepository.findByTaskUUIDAndUserUUID(taskuuid, useruuid);
        if(!projection.contains("taskworkerconstraintbudget")) return taskWorkerConstraint;

        List<TaskWorkerConstraint> taskWorkerConstraints = new ArrayList<>();
        taskWorkerConstraints.add(taskWorkerConstraint);
        return addTaskWorkerConstraintBudgetsToTaskWorkerConstraint(taskWorkerConstraints).get(0);
    }

    @RoleRight("tm.editor")
    public void create(TaskWorkerConstraint taskWorkerConstraint) throws SQLException {
        taskWorkerConstraintRepository.create(taskWorkerConstraint);
    }

    @RoleRight("tm.editor")
    public void update(TaskWorkerConstraint taskWorkerConstraint, String uuid) throws SQLException {
        taskWorkerConstraintRepository.update(taskWorkerConstraint, uuid);
    }

    private List<TaskWorkerConstraint> addTaskWorkerConstraintBudgetsToTaskWorkerConstraint(List<TaskWorkerConstraint> taskWorkerConstraints) {
        //Map<String, TaskWorkerConstraint> taskWorkerConstraintsMap = new HashMap<>();
        for (TaskWorkerConstraint taskWorkerConstraint : taskWorkerConstraints) {
            //taskWorkerConstraintsMap.put(taskWorkerConstraint.uuid, taskWorkerConstraint);

            for (TaskWorkerConstraintBudget taskWorkerConstraintBudget : taskWorkerConstraintBudgetService.findByTaskUUIDAndUserUUID(taskWorkerConstraint.useruuid, taskWorkerConstraint.taskuuid)) {
                taskWorkerConstraint.taskWorkerConstraintBudgets.add(taskWorkerConstraintBudget);
            }
        }

        //ArrayList<TaskWorkerConstraint> taskWorkerConstraintsResult = new ArrayList<>();
        //taskWorkerConstraintsResult.addAll(taskWorkerConstraintsMap.values());
        return taskWorkerConstraints;
    }

    private List<TaskWorkerConstraint> addUsersToTaskWorkerConstraint(List<TaskWorkerConstraint> taskWorkerConstraints) {
        Map<String, User> usersMap = userService.findAll().stream().collect(Collectors.toMap(User::getUuid, Function.identity()));
        for (TaskWorkerConstraint taskWorkerConstraint : taskWorkerConstraints) {
            taskWorkerConstraint.user = usersMap.get(taskWorkerConstraint.useruuid);
        }
        return taskWorkerConstraints;
    }
}
