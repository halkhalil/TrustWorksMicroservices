package dk.trustworks.bimanager.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dk.trustworks.bimanager.client.RestClient;
import dk.trustworks.bimanager.dto.*;
import dk.trustworks.bimanager.service.StatisticService;
import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;
import io.undertow.server.HttpServerExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by hans on 16/03/15.
 */
public class StatisticHandler extends DefaultHandler {

    private static final Logger log = LogManager.getLogger(StatisticHandler.class);
    private final StatisticService statisticService;
    private final RestClient restClient = new RestClient();

    Cache<String, List> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000).recordStats()
            .build();

    public StatisticHandler() {
        super("statistic");
        this.statisticService = new StatisticService();
        addCommand("revenueperday");
        addCommand("revenueperuser");
        addCommand("revenueperproject");
        addCommand("revenuepermonth");
        addCommand("budgetpermonth");
        addCommand("revenuepermonthbycapacity");
    }

    public void revenueperday(HttpServerExchange exchange, String[] params) {
        DateTime today = new DateTime();
        DateTime oneMonthAgo = new DateTime().minusDays(30);
        Interval pastMonth = new Interval(oneMonthAgo, today);
        List<Work> allWork = getAllWork(Calendar.getInstance().get(Calendar.YEAR));
        Map<String, TaskWorkerConstraint> taskWorkerConstraintMap = getTaskWorkerConstraintMap(getAllProjects());

        double revenueperday[] = new double[30];

        for (Work work : allWork) {
            DateTime workTime = new DateTime(work.getYear(), work.getMonth()+1, work.getDay(), 0, 0);
            if (!pastMonth.contains(workTime)) continue;
            int daysAgo = 29 - new Period(workTime, today).toStandardDays().getDays();
            TaskWorkerConstraint taskWorkerConstraint = taskWorkerConstraintMap.get(work.getUserUUID()+work.getTaskUUID());
            if(taskWorkerConstraint==null) continue;
            revenueperday[daysAgo] += work.getWorkDuration() * taskWorkerConstraint.getPrice();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("revenueperday", revenueperday);
        try {
            exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void revenuepermonth(HttpServerExchange exchange, String[] params) {
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());
        //int year = 2015;//new DateTime().getYear();
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
    }

    public void revenuepermonthbycapacity(HttpServerExchange exchange, String[] params) {
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());
        List<Work> allWork = getAllWork(year);
        Map<String, TaskWorkerConstraint> taskWorkerConstraintMap = getTaskWorkerConstraintMap(getAllProjects());
        int[] capacityPerMonth = getCapacityPerMonthByYear(year);
        int revenuepermonth[] = new int[12];

        for (Work work : allWork) {
            //if (work.getYear()!=year) continue;
            TaskWorkerConstraint taskWorkerConstraint = taskWorkerConstraintMap.get(work.getUserUUID()+work.getTaskUUID());
            if(taskWorkerConstraint==null) continue;
            revenuepermonth[work.getMonth()] += work.getWorkDuration() * taskWorkerConstraint.getPrice();
        }

        for (int i = 0; i < 12; i++) {
            revenuepermonth[i] = Math.round((revenuepermonth[i] / capacityPerMonth[i]) * 37);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("revenuepermonthbycapacity", revenuepermonth);
        try {
            exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void budgetpermonth(HttpServerExchange exchange, String[] params) {
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());
        //int year = 2015; //new DateTime().getYear();
        List<TaskWorkerConstraintBudget> allBudgets = getAllBudgets(year);

        double budgetpermonth[] = new double[12];

        for (TaskWorkerConstraintBudget taskWorkerConstraintBudget : allBudgets) {
            //if (taskWorkerConstraintBudget.getYear()!=year) continue;
            budgetpermonth[taskWorkerConstraintBudget.getMonth()] += taskWorkerConstraintBudget.getBudget();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("budgetpermonth", budgetpermonth);
        try {
            exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void revenueperuser(HttpServerExchange exchange, String[] params) {
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());
        System.out.println("StatisticHandler.revenueperuser");
        List<Work> allWork = getAllWork(year);
        Map<String, TaskWorkerConstraint> taskWorkerConstraintMap = getTaskWorkerConstraintMap(getAllProjects());
        Map<String, User> userMap = getAllUsersMap();

        Map<String, Double> revenuePerUser = new HashMap<>();
        List<AmountPerItem> listOfUsers = new ArrayList<>();

        for (Work work : allWork) {
            //if (!(work.getYear() == 2015)) continue;//new DateTime().getYear())) continue;
            TaskWorkerConstraint taskWorkerConstraint = taskWorkerConstraintMap.get(work.getUserUUID() + work.getTaskUUID());
            if (taskWorkerConstraint == null) continue;
            if (!revenuePerUser.containsKey(work.getUserUUID())) revenuePerUser.put(work.getUserUUID(), 0.0);
            revenuePerUser.put(work.getUserUUID(), revenuePerUser.get(work.getUserUUID()) + (work.getWorkDuration() * taskWorkerConstraint.getPrice()));
        }

        for (String userUUID : revenuePerUser.keySet()) {
            if(!userMap.containsKey(userUUID)) continue;
            listOfUsers.add(new AmountPerItem(userUUID, userMap.get(userUUID).getFirstname() + " " + userMap.get(userUUID).getLastname(), revenuePerUser.get(userUUID)));
        }

        try {
            exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(listOfUsers));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void revenueperproject(HttpServerExchange exchange, String[] params) {
        System.out.println("StatisticHandler.revenueperproject");
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());
        List<Work> allWork = getAllWork(year);
        Map<String, TaskWorkerConstraint> taskWorkerConstraintMap = getTaskWorkerConstraintMap(getAllProjects());

        Map<String, Double> revenuePerProject = new HashMap<>();
        List<AmountPerItem> listOfProjects = new ArrayList<>();

        for (Work work : allWork) {
            //if (!(work.getYear() == 2015)) continue;//new DateTime().getYear())) continue;
            TaskWorkerConstraint taskWorkerConstraint = taskWorkerConstraintMap.get(work.getUserUUID() + work.getTaskUUID());
            if (taskWorkerConstraint == null) continue;
            Project project = findProjectByTask(getAllProjects(), work.getTaskUUID());
            if (!revenuePerProject.containsKey(project.getUUID())) revenuePerProject.put(project.getUUID(), 0.0);
            revenuePerProject.put(project.getUUID(), revenuePerProject.get(project.getUUID()) + (work.getWorkDuration() * taskWorkerConstraint.getPrice()));
        }

        for (String projectUUID : revenuePerProject.keySet()) {
            Project project = findProjectByUUID(projectUUID);
            if(project == null) continue;
            Client client = findClientByUUID(project.getClientUUID());
            if(client == null) continue;
            listOfProjects.add(new AmountPerItem(projectUUID, client.name + " / " + project.getName(), revenuePerProject.get(projectUUID)));
        }

        try {
            exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(listOfProjects));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    class AmountPerItem {
        public String uuid;
        public String description;
        public double amount;

        public AmountPerItem() {
        }

        public AmountPerItem(String uuid, String description, double amount) {
            this.uuid = uuid;
            this.description = description;
            this.amount = amount;
        }
    }

    private Map<String, User> getAllUsersMap() {
        System.out.println("StatisticHandler.getAllUsersMap");
        Map<String, User> userMap = new HashMap<>();
        for (User user : restClient.getUsers()) {
            userMap.put(user.getUUID(), user);
        }
        return userMap;
    }

    @SuppressWarnings("unchecked")
    private List<Work> getAllWork(int year) {
        System.out.println("StatisticHandler.getAllWork");
        try {
            return cache.get("work"+year, () -> {
                Calendar calendar = Calendar.getInstance();
                List<Work> registeredWorkByYear = restClient.getRegisteredWorkByYear(year);
                //calendar.add(Calendar.YEAR, -1);
                //registeredWorkByYear.addAll(restClient.getRegisteredWorkByYear(calendar.get(Calendar.YEAR)));
                return registeredWorkByYear;
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @SuppressWarnings("unchecked")
    private List<TaskWorkerConstraintBudget> getAllBudgets(int year) {
        System.out.println("StatisticHandler.getAllBudgets");
        try {
            return cache.get("budgets"+year, () -> {
                List<TaskWorkerConstraintBudget> registeredBudgetByYear = restClient.getBudgetsByYear(year);
                return registeredBudgetByYear;
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private int[] getCapacityPerMonthByYear(int year) {
        System.out.println("StatisticHandler.getCapacityPerMonthByYear");
        System.out.println("year = [" + year + "]");
        int[] capacityPerMonth = restClient.getCapacityPerMonthByYear(year);
        return capacityPerMonth;
    }

    @SuppressWarnings("unchecked")
    private List<Project> getAllProjects() {
        System.out.println("StatisticHandler.getAllProjects");
        try {
            return cache.get("projects", restClient::getProjectsAndTasksAndTaskWorkerConstraints);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Client> getAllClients() {
        System.out.println("StatisticHandler.getAllClients");
        try {
            return cache.get("clients", restClient::getClients);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private Map<String, TaskWorkerConstraint> getTaskWorkerConstraintMap(List<Project> allProjects) {
        Map<String, TaskWorkerConstraint> taskWorkerConstraintMap = new HashMap<>();
        for (Project project : allProjects) {
            for (Task task : project.getTasks()) {
                for (TaskWorkerConstraint taskWorkerConstraint : task.getTaskWorkerConstraints()) {
                    taskWorkerConstraintMap.put(taskWorkerConstraint.getUserUUID()+taskWorkerConstraint.getTaskUUID(), taskWorkerConstraint);
                }
            }
        }
        return taskWorkerConstraintMap;
    }

    private Project findProjectByTask(List<Project> allProjects, String taskUUID) {
        System.out.println("StatisticHandler.findProjectByTask");
        for (Project project : allProjects) {
            for (Task task : project.getTasks()) {
                if(task.getUUID().equals(taskUUID)) return project;
            }
        }
        return null;
    }

    private Project findProjectByUUID(String projectUUID) {
        System.out.println("StatisticHandler.findProjectByUUID");
        for (Project project : getAllProjects()) {
            if(project.getUUID().equals(projectUUID)) return project;
        }
        return null;
    }

    private Client findClientByUUID(String clientUUID) {
        for (Client client : getAllClients()) {
            if(client.uuid.equals(clientUUID)) return client;
        }
        return null;
    }

    @Override
    protected DefaultLocalService getService() {
        return statisticService;
    }
}
