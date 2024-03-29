package dk.trustworks.bimanager.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import dk.trustworks.bimanager.caches.CacheHandler;
import dk.trustworks.bimanager.client.RestClient;
import dk.trustworks.bimanager.dto.TaskWorkerConstraint;
import dk.trustworks.bimanager.dto.TaskWorkerConstraintBudget;
import dk.trustworks.bimanager.dto.Work;
import dk.trustworks.bimanager.service.ProjectBudgetService;
import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;
import io.undertow.server.HttpServerExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Created by hans on 16/03/15.
 */
public class ProjectBudgetHandler extends DefaultHandler {

    private static final Logger log = LogManager.getLogger(ProjectBudgetHandler.class);
    private final ProjectBudgetService projectBudgetService;

    public ProjectBudgetHandler() {
        super("projectbudget");
        this.projectBudgetService = new ProjectBudgetService();
        addCommand("budgetcleanup");
        addCommand("budgetsbytask");
    }

    public void budgetsbytask(HttpServerExchange exchange, String[] params) {
        /*
        String projectUUID = exchange.getQueryParameters().get("projectUUID").getFirst();
        List<Work> allWork = getAllWork(year);
        Map<String, TaskWorkerConstraint> taskWorkerConstraintMap = getTaskWorkerConstraintMap(getAllProjects());

        double revenuepermonth[] = new double[12];

        for (Work work : allWork) {
            //if (work.getYear()!=year) continue;
            TaskWorkerConstraint taskWorkerConstraint = taskWorkerConstraintMap.get(work.getUserUUID()+work.getTaskUUID());
            if(taskWorkerConstraint==null) continue;
            revenuepermonth[work.getMonth()] += work.getWorkDuration() * taskWorkerConstraint.getPrice();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("revenuepermonth", revenuepermonth);
        try {
            exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        */
    }

    public void budgetcleanup(HttpServerExchange exchange, String[] params) {
        System.out.println("ProjectBudgetHandler.budgetcleanup");
        int month = Integer.parseInt(exchange.getQueryParameters().get("month").getFirst());
        System.out.println("month = " + month);
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());
        System.out.println("year = " + year);

        RestClient restClient = new RestClient();
        List<TaskWorkerConstraintBudget> workBudgets = new ArrayList<>();
        Map<String, TaskWorkerConstraintBudget> workBudgetMap = new HashMap<>();
        System.out.println("workBudgetMap.size() = " + workBudgetMap.size());
        for (Work work : restClient.getRegisteredWorkByMonth(year, month)) {
            TaskWorkerConstraint taskWorkerConstraint = restClient.getTaskWorkerConstraint(work.getTaskUUID(), work.getUserUUID());
            //System.out.println("work (" + taskWorkerConstraint.getUuid() + ") = " + work);
            //System.out.println("taskWorkerConstraint = " + taskWorkerConstraint);
            if (!workBudgetMap.containsKey(taskWorkerConstraint.getUUID())) {
                workBudgetMap.put(taskWorkerConstraint.getUUID(), new TaskWorkerConstraintBudget(0.0, month, "", UUID.randomUUID().toString(), year));
            }
            TaskWorkerConstraintBudget currentBudget = workBudgetMap.get(taskWorkerConstraint.getUUID());
            if (taskWorkerConstraint.getUUID() == null) System.err.println("LOG00180: " + taskWorkerConstraint);
            currentBudget.setBudget(currentBudget.getBudget() + (work.getWorkDuration() * taskWorkerConstraint.getPrice()));
            currentBudget.setTaskWorkerConstraintUUID(taskWorkerConstraint.getUUID());
            currentBudget.setTaskWorkerConstraint(taskWorkerConstraint);
            System.out.println("currentBudget (" + taskWorkerConstraint.getUUID() + ") = " + currentBudget);
        }
        workBudgets.addAll(workBudgetMap.values());
        for (TaskWorkerConstraintBudget workBudget : workBudgets) {
            System.out.println("workBudget = " + workBudget);
        }


        List<TaskWorkerConstraintBudget> actualBudgets = restClient.getBudgetsByMonthAndYear(month, year);
        List<TaskWorkerConstraintBudget> newBudgets = new ArrayList<>();
        List<TaskWorkerConstraintBudget> newRemoveList = new ArrayList<>();
        for (TaskWorkerConstraintBudget newBudget : workBudgetMap.values()) {
            List<TaskWorkerConstraintBudget> actualRemoveList = new ArrayList<>();
            actualBudgets.stream().filter(actualBudget -> actualBudget.getTaskWorkerConstraintUUID().equals(newBudget.getTaskWorkerConstraintUUID()) &&
                    actualBudget.getYear() == newBudget.getYear() &&
                    actualBudget.getMonth() == newBudget.getMonth()).forEach(actualBudget -> {
                actualBudget.setBudget(newBudget.getBudget());
                newBudgets.add(actualBudget);
                actualRemoveList.add(actualBudget);
                newRemoveList.add(newBudget);
            });
            actualBudgets.removeAll(actualRemoveList);
        }
        workBudgets.removeAll(newRemoveList);
        newBudgets.addAll(workBudgets);

        for (TaskWorkerConstraintBudget actualBudget : actualBudgets) {
            actualBudget.setBudget(0.0);
            newBudgets.add(actualBudget);
        }

        newBudgets.forEach(restClient::postTaskBudget);

        log.exit();
    }

    public Cache<String, List> getListCache() {
        return CacheHandler.createCacheHandler().getListCache();
    }

    public Cache<String, Map> getMapCache() {
        return CacheHandler.createCacheHandler().getMapCache();
    }

    @Override
    protected DefaultLocalService getService() {
        return projectBudgetService;
    }
}
