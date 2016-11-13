package dk.trustworks.clientmanager.service;

import dk.trustworks.clientmanager.model.TaskWorkerConstraint;
import dk.trustworks.clientmanager.model.TaskWorkerConstraintBudget;
import dk.trustworks.clientmanager.persistence.TaskWorkerConstraintBudgetRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by hans on 17/03/15.
 */
public class TaskWorkerConstraintBudgetService {

    private static final Logger logger = LogManager.getLogger();

    private TaskWorkerConstraintBudgetRepository taskWorkerConstraintBudgetRepository;
    private TaskWorkerConstraintService taskWorkerConstraintService;

    public TaskWorkerConstraintBudgetService(DataSource ds) {
        taskWorkerConstraintBudgetRepository = new TaskWorkerConstraintBudgetRepository(ds);
        taskWorkerConstraintService = new TaskWorkerConstraintService(ds);
    }

    public List<TaskWorkerConstraintBudget> findByPeriod(LocalDate fromDate, LocalDate toDate, int ahead) {
        List<TaskWorkerConstraintBudget> result = new ArrayList<>();

        LocalDate currentDate = fromDate;
        while(currentDate.isBefore(toDate)) {
            LocalDate entryDate = (ahead > 0)?currentDate.minusMonths(ahead):LocalDate.now();
            result.addAll(taskWorkerConstraintBudgetRepository.findByMonthAndYearAndDate(currentDate.getMonthOfYear()-1, currentDate.getYear(), entryDate));
            currentDate.plusMonths(1);
        }
        return result;
    }

/*
    public List<TaskWorkerConstraintBudget> findByTaskWorkerConstraintUUID(String taskworkerconstraintuuid) {
        return taskWorkerConstraintBudgetRepository.findByTaskWorkerConstraintUUID(taskworkerconstraintuuid);
    }

    public List<TaskWorkerConstraintBudget> findByMonthAndYear(int month, int year) {
        logger.debug("TaskWorkerConstraintBudgetService.findByMonthAndYear");
        return taskWorkerConstraintBudgetRepository.findByMonthAndYear(month, year);
    }


    public List<TaskWorkerConstraintBudget> findByYear(int year, int ahead) {
        logger.debug("TaskWorkerConstraintBudgetService.findByYear");
        //if(ahead.isPresent()) {
            List<TaskWorkerConstraintBudget> result = new ArrayList<>();
            for (int month = 1; month < 13; month++) {
                LocalDate localDate = new LocalDate(year, month, 1).minusMonths(ahead);
                result.addAll(taskWorkerConstraintBudgetRepository.findByMonthAndYearAndDate(month-1, year, localDate));
            }
            System.out.println("result.size() = " + result.size());
            return result;
        //}
        //return taskWorkerConstraintBudgetRepository.findByYear(year);
    }

    public List<TaskWorkerConstraintBudget> findByYearAndUser(int year, String userUUID) {
        logger.debug("TaskWorkerConstraintBudgetService.findByYear");
        return taskWorkerConstraintBudgetRepository.findByYearAndUser(year, userUUID);
    }

    public List<TaskWorkerConstraintBudget> findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(String taskworkerconstraintuuid, int month, int year, LocalDate date) {
        logger.debug("TaskWorkerConstraintBudgetService.findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate");
        return taskWorkerConstraintBudgetRepository.findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(taskworkerconstraintuuid, month, year, date);
    }
*/
    public void create(TaskWorkerConstraintBudget taskWorkerConstraintBudget) throws SQLException {
        taskWorkerConstraintBudgetRepository.create(taskWorkerConstraintBudget);
    }

    public void update(TaskWorkerConstraintBudget taskWorkerConstraintBudget, String uuid) throws SQLException {
        taskWorkerConstraintBudgetRepository.update(taskWorkerConstraintBudget, uuid);
    }

    public void addUserTask() {
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
