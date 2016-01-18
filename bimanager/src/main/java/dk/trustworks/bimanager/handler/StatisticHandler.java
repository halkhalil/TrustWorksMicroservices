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

    private final Cache<String, List> cache = CacheBuilder.newBuilder()
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
        addCommand("billablehoursperuser");
        addCommand("billablehoursperuserperday");
        addCommand("revenuepermonthperuser");
        addCommand("budgetpermonthperuser");
        addCommand("sickdayspermonthperuser");
        addCommand("freedayspermonthperuser");
        addCommand("workregisterdelay");
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

    public void billablehoursperuserperday(HttpServerExchange exchange, String[] params) {
        System.out.println("StatisticHandler.billablehoursperuserperday");
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());
        System.out.println("year = " + year);
        String userUUID = exchange.getQueryParameters().get("useruuid").getFirst();
        System.out.println("userUUID = " + userUUID);
        List<Work> allWork = getAllWork(year);

        Map<String, TaskWorkerConstraint> taskWorkerConstraintMap = getTaskWorkerConstraintMap(getAllProjects());

        double billablehoursperday[] = new double[7];

        for (Work work : allWork) {
            if(!userUUID.equals(work.getUserUUID())) continue;
            TaskWorkerConstraint taskWorkerConstraint = taskWorkerConstraintMap.get(work.getUserUUID()+work.getTaskUUID());
            if(taskWorkerConstraint==null || taskWorkerConstraint.getPrice() <= 0) continue;
            DateTime dateTime = new DateTime(work.getYear(), work.getMonth()+1, work.getDay(), 0, 0);
            billablehoursperday[dateTime.getDayOfWeek()-1] += work.getWorkDuration();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("billablehoursperday", billablehoursperday);
        try {
            exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void revenuepermonth(HttpServerExchange exchange, String[] params) {
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());
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

    public void revenuepermonthperuser(HttpServerExchange exchange, String[] params) {
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());
        System.out.println("year = " + year);
        String userUUID = exchange.getQueryParameters().get("useruuid").getFirst();
        System.out.println("userUUID = " + userUUID);
        List<Work> allWork = getAllWork(year);
        Map<String, TaskWorkerConstraint> taskWorkerConstraintMap = getTaskWorkerConstraintMap(getAllProjects());

        double revenuepermonth[] = new double[12];

        for (Work work : allWork) {
            if(!work.getUserUUID().equals(userUUID)) continue;
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
            if(capacityPerMonth[i] == 0) continue;
            if(revenuepermonth[i] == 0) continue;
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
        List<TaskWorkerConstraintBudget> allBudgets = getAllBudgets(year);

        double budgetpermonth[] = new double[12];

        for (TaskWorkerConstraintBudget taskWorkerConstraintBudget : allBudgets) {
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

    public void budgetpermonthperuser(HttpServerExchange exchange, String[] params) {
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());
        System.out.println("year = " + year);
        String userUUID = exchange.getQueryParameters().get("useruuid").getFirst();
        System.out.println("userUUID = " + userUUID);
        List<TaskWorkerConstraintBudget> allBudgets = getAllBudgetsByUser(year, userUUID);

        double budgetpermonth[] = new double[12];

        for (TaskWorkerConstraintBudget taskWorkerConstraintBudget : allBudgets) {
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

    public void sickdayspermonthperuser(HttpServerExchange exchange, String[] params) {
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());
        System.out.println("year = " + year);
        String userUUID = exchange.getQueryParameters().get("useruuid").getFirst();
        System.out.println("userUUID = " + userUUID);
        List<Work> allWork = getAllWork(year);

        double sickdaysPerMonth[] = new double[12];

        for (Work work : allWork) {
            if(work.getUserUUID().equals(userUUID) && work.getTaskUUID().equals("02bf71c5-f588-46cf-9695-5864020eb1c4")) {
                if(work.getWorkDuration() > 0)
                    sickdaysPerMonth[work.getMonth()] += 1;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("sickdayspermonth", sickdaysPerMonth);
        try {
            exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void freedayspermonthperuser(HttpServerExchange exchange, String[] params) {
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());
        System.out.println("year = " + year);
        String userUUID = exchange.getQueryParameters().get("useruuid").getFirst();
        System.out.println("userUUID = " + userUUID);
        List<Work> allWork = getAllWork(year);

        double freedaysPerMonth[] = new double[12];

        for (Work work : allWork) {
            if(work.getUserUUID().equals(userUUID) && work.getTaskUUID().equals("f585f46f-19c1-4a3a-9ebd-1a4f21007282")) {
                if(work.getWorkDuration() > 0)
                    freedaysPerMonth[work.getMonth()] += 1;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("freedayspermonth", freedaysPerMonth);
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

    public void billablehoursperuser(HttpServerExchange exchange, String[] params) {
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());
        System.out.println("StatisticHandler.billablehoursperuser");
        List<Work> allWork = getAllWork(year);
        Map<String, TaskWorkerConstraint> taskWorkerConstraintMap = getTaskWorkerConstraintMap(getAllProjects());
        Map<String, User> userMap = getAllUsersMap();

        Map<String, Double> revenuePerUser = new HashMap<>();
        List<AmountPerItem> listOfUsers = new ArrayList<>();

        for (Work work : allWork) {
            TaskWorkerConstraint taskWorkerConstraint = taskWorkerConstraintMap.get(work.getUserUUID() + work.getTaskUUID());
            if (taskWorkerConstraint == null) continue;
            if (taskWorkerConstraint.getPrice() <= 0) continue;
            if (!revenuePerUser.containsKey(work.getUserUUID())) revenuePerUser.put(work.getUserUUID(), 0.0);
            revenuePerUser.put(work.getUserUUID(), revenuePerUser.get(work.getUserUUID()) + (work.getWorkDuration()));
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

    public void workregisterdelay(HttpServerExchange exchange, String[] params) {
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());
        System.out.println("year = " + year);
        List<Work> allWork = getAllWork(year);

        HashMap<String, int[]> listOfDays = new HashMap<>();

        int i = 0;
        for (Work work : allWork) {
            if(work.getWorkDuration() > 0) {
                if(listOfDays.get(work.getUserUUID()) == null) listOfDays.put(work.getUserUUID(), new int[365]);
                int[] delayPerMonth = listOfDays.get(work.getUserUUID());
                DateTime workDate = new DateTime(work.getYear(), work.getMonth()+1, work.getDay(), 0, 0);
                DateTime registeredDate = new DateTime(work.getCreated());
                if(registeredDate.isBefore(new DateTime(2015, 7, 1, 0 ,0))) continue;

                Period period = new Period(workDate, registeredDate);
                if(delayPerMonth[i] == 0 || delayPerMonth[i] < period.getDays()) delayPerMonth[i] = period.getDays();
            }
        }

        double avgDelay = 0.0;
        int count = 0;
        ArrayList<AmountPerItem> amountPerItems = new ArrayList<>();

        Map<String, User> usersMap = getAllUsersMap();
        for (String userUUID : listOfDays.keySet()) {
            int[] delayPerDay = listOfDays.get(userUUID);
            for (int delay : delayPerDay) {
                if(delay > 0) {
                    count++;
                    avgDelay += delay;
                }
            }
            avgDelay = avgDelay / count;
            amountPerItems.add(new AmountPerItem(userUUID, usersMap.get(userUUID).getFirstname() + " " + usersMap.get(userUUID).getLastname(), avgDelay));
        }
        try {
            exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(amountPerItems));
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
                List<Work> registeredWorkByYear = restClient.getRegisteredWorkByYear(year);
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

    @SuppressWarnings("unchecked")
    private List<TaskWorkerConstraintBudget> getAllBudgetsByUser(int year, String userUUID) {
        System.out.println("StatisticHandler.getAllBudgetsByUser");
        try {
            return cache.get("budgets"+year+userUUID, () -> {
                List<TaskWorkerConstraintBudget> registeredBudgetByYear = restClient.getBudgetsByYearAndUser(year, userUUID);
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
