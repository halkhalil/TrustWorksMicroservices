package dk.trustworks.clientmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.model.TaskWorkerConstraint;
import dk.trustworks.framework.model.TaskWorkerConstraintBudget;
import dk.trustworks.clientmanager.persistence.ProjectRepository;
import dk.trustworks.clientmanager.persistence.TaskRepository;
import dk.trustworks.clientmanager.persistence.TaskWorkerConstraintBudgetRepository;
import dk.trustworks.clientmanager.persistence.TaskWorkerConstraintRepository;
import dk.trustworks.framework.model.Project;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * Created by hans on 17/03/15.
 */
public class ProjectBudgetService extends DefaultLocalService {

    private static final Logger log = LogManager.getLogger();

    private ProjectRepository projectRepository;
    private TaskRepository taskRepository;
    private TaskWorkerConstraintRepository taskWorkerConstraintRepository;
    private TaskWorkerConstraintBudgetRepository taskWorkerConstraintBudgetRepository;

    public ProjectBudgetService() {
        projectRepository = new ProjectRepository(null);
        //taskRepository = new TaskRepository();
        //taskWorkerConstraintRepository = new TaskWorkerConstraintRepository();
        //taskWorkerConstraintBudgetRepository = new TaskWorkerConstraintBudgetRepository();
    }

    public List<Map<String, Object>> findByYear(Map<String, Deque<String>> queryParameters) {
        log.debug("ProjectBudgetService.findByPeriod");
        log.debug("queryParameters = [" + queryParameters + "]");
        long allTimer = System.currentTimeMillis();
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        try {
            List<Map<String, Object>> projectYearBudgets = new ArrayList<>();
            long allWorkTimer = System.currentTimeMillis();
            log.debug("Load all work: {}", (System.currentTimeMillis() - allWorkTimer));

            long timer = System.currentTimeMillis();

            List<Project> projectsAndTasksAndTaskWorkerConstraints = projectRepository.findAll();
            log.debug("projectsAndTasksAndTaskWorkerConstraints: {}", (System.currentTimeMillis() - timer));

            StreamSupport.stream(projectsAndTasksAndTaskWorkerConstraints.spliterator(), true).map((project) -> {
                Map<String, Object> budgetSummary = new HashMap<>();
                budgetSummary.put("projectuuid", project.uuid);
                budgetSummary.put("projectname", project.name);
                budgetSummary.put("amount", new double[12]);
                List<Map<String, Object>> tasks = null;//taskRepository.findByProjectUUID((String) project.uuid);
                for (int month = 0; month < 12; month++) {
                    for (Map<String, Object> task : tasks) {
                        for (TaskWorkerConstraint taskWorkerConstraint : taskWorkerConstraintRepository.findByTaskUUID((String) task.get("uuid"))) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, 1, 0, 0);

                            calendar.roll(Calendar.MONTH, -2);
                            calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
                            if (year < 2015 || (year < 2016 && month < 7)) calendar = Calendar.getInstance();
                            if (year > Calendar.getInstance().get(Calendar.YEAR)) calendar = Calendar.getInstance();
                            if (year == Calendar.getInstance().get(Calendar.YEAR) && month >= Calendar.getInstance().get(Calendar.MONTH))
                                calendar = Calendar.getInstance();

                            /*
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, 1, 0, 0);
                            if (year < 2015 || (year < 2016 && month < 6)) calendar = Calendar.getInstance();
                            if (year > Calendar.getInstance().get(Calendar.YEAR)) calendar = Calendar.getInstance();
                            if (year == Calendar.getInstance().get(Calendar.YEAR) && month >= Calendar.getInstance().get(Calendar.MONTH)) calendar = Calendar.getInstance();*/
                            List<TaskWorkerConstraintBudget> budgets = null;//taskWorkerConstraintBudgetRepository.findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(taskWorkerConstraint.uuid.toString(), month, year, LocalDate.now());
                            if (taskWorkerConstraint.uuid.equals("6af071fa-6a95-44e5-8634-9820e0887500") && month == 7) {
                                System.out.println("calendar = " + calendar.getTime());
                                System.out.println("budgets.get(\"budget\") = " + budgets.get(0).budget);
                            }
                            if (budgets.size() > 0)
                                ((double[]) budgetSummary.get("amount"))[month] += budgets.get(0).budget;
                        }
                    }
                }
                return budgetSummary;
            }).forEach(projectYearBudgets::add);
            log.debug("Load all: {}", (System.currentTimeMillis() - allTimer));
            return projectYearBudgets;
        } catch (Exception e) {
            log.error("LOG00840:", e);
        }
        return null;
    }

    @Deprecated
    public List<Map<String, Object>> findByUserAndYear(Map<String, Deque<String>> queryParameters) {
        log.debug("ProjectBudgetService.findByPeriod");
        log.debug("queryParameters = [" + queryParameters + "]");
        final int year = Integer.parseInt(queryParameters.get("year").getFirst());
        final String userUUID = queryParameters.get("useruuid").getFirst();
        try {
            return getProjectYearBudgets(year, userUUID, true);
        } catch (Exception e) {
            log.error("LOG00840:", e);
        }
        return null;
    }

    @Deprecated
    public List<Map<String, Object>> findByUserAndYearAndHours(Map<String, Deque<String>> queryParameters) {
        log.debug("ProjectBudgetService.findByPeriod");
        log.debug("queryParameters = [" + queryParameters + "]");
        final int year = Integer.parseInt(queryParameters.get("year").getFirst());
        final String userUUID = queryParameters.get("useruuid").getFirst();
        try {
            return getProjectYearBudgets(year, userUUID, false);
        } catch (Exception e) {
            log.error("LOG00840:", e);
        }
        return null;
    }

    @Deprecated
    private List<Map<String, Object>> getProjectYearBudgets(int year, String userUUID, boolean useRate) {
        long allTimer = System.currentTimeMillis();

        List<Map<String, Object>> projectYearBudgets = new ArrayList<>();
        return projectYearBudgets;
        /*
        long allWorkTimer = System.currentTimeMillis();
        log.debug("Load all work: {}", (System.currentTimeMillis() - allWorkTimer));

        long timer = System.currentTimeMillis();

        List<Map<String, Object>> projectsAndTasksAndTaskWorkerConstraints = projectRepository.getAllEntities("project");
        log.debug("projectsAndTasksAndTaskWorkerConstraints: {}", (System.currentTimeMillis() - timer));

        StreamSupport.stream(projectsAndTasksAndTaskWorkerConstraints.spliterator(), true).map((project) -> {
            Map<String, Object> budgetSummary = new HashMap<>();
            budgetSummary.put("projectuuid", project.get("uuid"));
            budgetSummary.put("projectname", project.get("name"));
            budgetSummary.put("amount", new double[12]);
            List<Map<String, Object>> tasks = taskRepository.findByProjectUUID((String) project.get("uuid"));
            for (int month = 0; month < 12; month++) {
                for (Map<String, Object> task : tasks) {
                    Map<String, Object> taskWorkerConstraint = taskWorkerConstraintRepository.findByTaskUUIDAndUserUUID((String) task.get("uuid"), userUUID);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, 1, 0, 0);

                    calendar.roll(Calendar.MONTH, false);
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
                    if (year < 2015 || (year < 2016 && month < 6)) calendar = Calendar.getInstance();
                    if (year > Calendar.getInstance().get(Calendar.YEAR)) calendar = Calendar.getInstance();
                    if (year == Calendar.getInstance().get(Calendar.YEAR) && month >= Calendar.getInstance().get(Calendar.MONTH))
                        calendar = Calendar.getInstance();
                    List<Map<String, Object>> budgets = taskWorkerConstraintBudgetRepository.findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(taskWorkerConstraint.get("uuid").toString(), month, year, calendar.getTime());
                    if (taskWorkerConstraint.get("uuid").equals("6af071fa-6a95-44e5-8634-9820e0887500") && month == 7) {
                        System.out.println("calendar = " + calendar.getTime());
                        System.out.println("budgets.get(\"budget\") = " + budgets.get(0).get("budget"));
                    }
                    if (budgets.size() > 0) {
                        if (useRate)
                            ((double[]) budgetSummary.get("amount"))[month] += (double) budgets.get(0).get("budget");
                        else
                            ((double[]) budgetSummary.get("amount"))[month] += ((double) budgets.get(0).get("budget") / (double) taskWorkerConstraint.get("price"));
                    }
                }
            }
            return budgetSummary;
        }).forEach(projectYearBudgets::add);
        log.debug("Load all: {}", (System.currentTimeMillis() - allTimer));
        return projectYearBudgets;
        */
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        log.debug("ProjectService.create");
        throw new RuntimeException("Not allowed");
    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        log.debug("ProjectService.update");
        throw new RuntimeException("Not allowed");
    }

    @Override
    public GenericRepository getGenericRepository() {
        throw new RuntimeException("Not allowed");
    }

    @Override
    public String getResourcePath() {
        return "projectbudget";
    }
}
