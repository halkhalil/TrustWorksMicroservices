package dk.trustworks.clientmanager.service;

import dk.trustworks.framework.model.Task;
import dk.trustworks.framework.model.TaskWorkerConstraint;
import dk.trustworks.framework.model.TaskWorkerConstraintBudget;
import dk.trustworks.clientmanager.persistence.TaskWorkerConstraintBudgetRepository;
import dk.trustworks.framework.security.Authenticator;
import dk.trustworks.framework.security.RoleRight;
import net.sf.cglib.proxy.Enhancer;
import org.joda.time.LocalDate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 17/03/15.
 */
public class TaskWorkerConstraintBudgetService {

    private TaskWorkerConstraintBudgetRepository taskWorkerConstraintBudgetRepository;
    private DataSource ds;
    //private TaskWorkerConstraintService taskWorkerConstraintService;


    public TaskWorkerConstraintBudgetService() {
    }

    public TaskWorkerConstraintBudgetService(DataSource ds) {
        taskWorkerConstraintBudgetRepository = new TaskWorkerConstraintBudgetRepository(ds);
        //taskWorkerConstraintService = new TaskWorkerConstraintService(ds);
        this.ds = ds;
    }

    public static TaskWorkerConstraintBudgetService getInstance(DataSource ds) {
        TaskWorkerConstraintBudgetService service = new TaskWorkerConstraintBudgetService(ds);
        return (TaskWorkerConstraintBudgetService) Enhancer.create(service.getClass(), new Authenticator(service));
    }

    public List<TaskWorkerConstraintBudget> findByPeriod(LocalDate fromDate, LocalDate toDate, int ahead) {
        List<TaskWorkerConstraintBudget> result = new ArrayList<>();

        LocalDate currentDate = fromDate;
        while(currentDate.isBefore(toDate)) {
            LocalDate entryDate = (ahead > 0)?currentDate.minusMonths(ahead):LocalDate.now();
            result.addAll(taskWorkerConstraintBudgetRepository.findByMonthAndYearAndDate(currentDate.getMonthOfYear()-1, currentDate.getYear(), entryDate));
            currentDate = currentDate.plusMonths(1);
        }
        return result;
    }

    @Deprecated
    @RoleRight("tm.user")
    public List<TaskWorkerConstraintBudget> findByTaskUUID(String taskUUID) {
        return taskWorkerConstraintBudgetRepository.findByTaskUUID(taskUUID);
    }

    @RoleRight("tm.user")
    public List<String> getUniqueUsersBudgetsPerTask(String taskUUID) {
        return taskWorkerConstraintBudgetRepository.getUniqueUsersBudgetsPerTask(taskUUID);
    }

    @RoleRight("tm.user")
    public List<TaskWorkerConstraintBudget> findByTaskUUIDsWithHistory(List<Task> tasks) {
        return taskWorkerConstraintBudgetRepository.findByTaskUUIDsWithHistory(tasks);
    }

    @RoleRight("tm.user")
    public List<TaskWorkerConstraintBudget> findByTaskUUIDAndUserUUID(String userUUID, String taskUUID) {
        return taskWorkerConstraintBudgetRepository.findByTaskUUIDAndUserUUID(taskUUID, userUUID);
    }
/*
    public List<TaskWorkerConstraintBudget> findByMonthAndYear(int month, int year) {
        logger.debug("TaskWorkerConstraintBudgetService.findByMonthAndYear");
        return taskWorkerConstraintBudgetRepository.findByMonthAndYear(month, year);
    }


    public List<TaskWorkerConstraintBudget> findByPeriod(int year, int ahead) {
        logger.debug("TaskWorkerConstraintBudgetService.findByPeriod");
        //if(ahead.isPresent()) {
            List<TaskWorkerConstraintBudget> result = new ArrayList<>();
            for (int month = 1; month < 13; month++) {
                LocalDate localDate = new LocalDate(year, month, 1).minusMonths(ahead);
                result.addAll(taskWorkerConstraintBudgetRepository.findByMonthAndYearAndDate(month-1, year, localDate));
            }
            System.out.println("result.size() = " + result.size());
            return result;
        //}
        //return taskWorkerConstraintBudgetRepository.findByPeriod(year);
    }

    public List<TaskWorkerConstraintBudget> findByYearAndUser(int year, String userUUID) {
        logger.debug("TaskWorkerConstraintBudgetService.findByPeriod");
        return taskWorkerConstraintBudgetRepository.findByYearAndUser(year, userUUID);
    }

    public List<TaskWorkerConstraintBudget> findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(String taskworkerconstraintuuid, int month, int year, LocalDate date) {
        logger.debug("TaskWorkerConstraintBudgetService.findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate");
        return taskWorkerConstraintBudgetRepository.findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(taskworkerconstraintuuid, month, year, date);
    }
*/

    @RoleRight("tm.editor")
    public void create(TaskWorkerConstraintBudget taskWorkerConstraintBudget) throws SQLException {
        taskWorkerConstraintBudgetRepository.create(taskWorkerConstraintBudget);
    }

    @RoleRight("tm.editor")
    public void update(TaskWorkerConstraintBudget taskWorkerConstraintBudget, String uuid) throws SQLException {
        taskWorkerConstraintBudgetRepository.update(taskWorkerConstraintBudget, uuid);
    }

    public void addUserTask() {
        TaskWorkerConstraintService taskWorkerConstraintService = new TaskWorkerConstraintService(ds);
        for (TaskWorkerConstraintBudget taskWorkerConstraintBudget : taskWorkerConstraintBudgetRepository.findAll()) {
            if(taskWorkerConstraintBudget.taskworkerconstraintuuid.trim().equals("")) continue;
            TaskWorkerConstraint taskWorkerConstraint = taskWorkerConstraintService.findByUUID(taskWorkerConstraintBudget.taskworkerconstraintuuid);
            if(taskWorkerConstraint == null) continue;
            taskWorkerConstraintBudget.useruuid = taskWorkerConstraint.useruuid;
            taskWorkerConstraintBudget.taskuuid = taskWorkerConstraint.taskuuid;
            taskWorkerConstraintBudgetRepository.addUserTask(taskWorkerConstraintBudget);
        }
    }
}
