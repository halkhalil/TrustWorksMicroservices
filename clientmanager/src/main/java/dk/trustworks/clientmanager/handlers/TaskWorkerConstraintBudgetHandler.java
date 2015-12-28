package dk.trustworks.clientmanager.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.trustworks.clientmanager.persistence.TaskWorkerConstraintBudgetRepository;
import dk.trustworks.clientmanager.service.TaskWorkerConstraintBudgetService;
import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;
import io.undertow.server.HttpServerExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 16/03/15.
 */
public class TaskWorkerConstraintBudgetHandler extends DefaultHandler {

    private static final Logger log = LogManager.getLogger(TaskWorkerConstraintBudgetHandler.class);
    private final TaskWorkerConstraintBudgetService taskWorkerConstraintBudgetService;
    private final TaskWorkerConstraintBudgetRepository taskWorkerConstraintBudgetRepository;

    public TaskWorkerConstraintBudgetHandler() {
        super("taskworkerconstraintbudget");
        this.taskWorkerConstraintBudgetService = new TaskWorkerConstraintBudgetService();
        this.taskWorkerConstraintBudgetRepository = new TaskWorkerConstraintBudgetRepository();
        addCommand("calculatetotalbudget");
    }

    public void calculatetotalbudget(HttpServerExchange exchange, String[] params) {
        log.debug("TaskWorkerConstraintBudgetHandler.calculatetotalbudget");
        log.debug("exchange = [" + exchange + "], params = [" + params + "]");
        double totalWorkDuration = taskWorkerConstraintBudgetRepository.calculateTotalTaskBudget(exchange.getQueryParameters().get("taskuuid").getFirst());
        Map<String, Object> result = new HashMap<>();
        result.put("totalworkduration", totalWorkDuration);
        try {
            exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected DefaultLocalService getService() {
        return taskWorkerConstraintBudgetService;
    }
}
