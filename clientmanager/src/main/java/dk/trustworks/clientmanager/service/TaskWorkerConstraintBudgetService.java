package dk.trustworks.clientmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.clientmanager.persistence.TaskWorkerConstraintBudgetRepository;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 17/03/15.
 */
public class TaskWorkerConstraintBudgetService extends DefaultLocalService {

    private static final Logger logger = LogManager.getLogger();

    private TaskWorkerConstraintBudgetRepository taskWorkerConstraintBudgetRepository;

    public TaskWorkerConstraintBudgetService() {
        taskWorkerConstraintBudgetRepository = new TaskWorkerConstraintBudgetRepository();
    }

    public List<Map<String, Object>> findByTaskWorkerConstraintUUID(Map<String, Deque<String>> queryParameters) {
        logger.debug("TaskWorkerConstraintBudgetService.findByTaskWorkerConstraintUUID");
        String taskworkerconstraintuuid = queryParameters.get("taskworkerconstraintuuid").getFirst();
        return taskWorkerConstraintBudgetRepository.findByTaskWorkerConstraintUUID(taskworkerconstraintuuid);
    }

    public List<Map<String, Object>> findByMonthAndYear(Map<String, Deque<String>> queryParameters) {
        logger.debug("TaskWorkerConstraintBudgetService.findByMonthAndYear");
        int month = Integer.parseInt(queryParameters.get("month").getFirst());
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        return taskWorkerConstraintBudgetRepository.findByMonthAndYear(month, year);
    }

    public List<Map<String, Object>> findByYear(Map<String, Deque<String>> queryParameters) {
        logger.debug("TaskWorkerConstraintBudgetService.findByYear");
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        return taskWorkerConstraintBudgetRepository.findByYear(year);
    }

    public List<Map<String, Object>> findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(Map<String, Deque<String>> queryParameters) {
        logger.debug("TaskWorkerConstraintBudgetService.findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate");
        String taskworkerconstraintuuid = queryParameters.get("taskworkerconstraintuuid").getFirst();
        int month = Integer.parseInt(queryParameters.get("month").getFirst());
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        Date datetime = new Date(Long.parseLong(queryParameters.get("datetime").getFirst()));
        return taskWorkerConstraintBudgetRepository.findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(taskworkerconstraintuuid, month, year, datetime);
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        taskWorkerConstraintBudgetRepository.create(jsonNode);
    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        taskWorkerConstraintBudgetRepository.update(jsonNode, uuid);
    }

    @Override
    public GenericRepository getGenericRepository() {
        return taskWorkerConstraintBudgetRepository;
    }

    @Override
    public String getResourcePath() {
        return "taskworkerconstraintbudget";
    }
}
