package dk.trustworks.bimanager.client;

import com.google.common.cache.Cache;
import dk.trustworks.bimanager.caches.CacheHandler;
import dk.trustworks.bimanager.dto.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class RestDelegate {
    private final RestClient restClient;
    private final CacheHandler cacheHandler;
    private final Cache<String, List> listCache;
    public static RestDelegate instance;

    private RestDelegate() {
        this.restClient = new RestClient();
        cacheHandler = CacheHandler.createCacheHandler();
        listCache = CacheHandler.createCacheHandler().getListCache();
    }

    public static final RestDelegate getInstance() {
        return ((instance!=null)?instance:(instance = new RestDelegate()));
    }

    public Map<String, User> getAllUsersMap() {
        try {
            return cacheHandler.getMapCache().get("users", () -> {
                Map<String, User> userMap = new HashMap<String, User>();
                for (User user : restClient.getUsers()) {
                    userMap.put(user.getUUID(), user);
                }
                return userMap;
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Work> getAllWork(int year) {
        try {
            return listCache.get("work"+year, () -> restClient.getRegisteredWorkByYear(year));
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @SuppressWarnings("unchecked")
    public List<TaskWorkerConstraintBudget> getAllBudgets(int year, int ahead) {
        try {
            return listCache.get("budgets"+year+ahead, () -> restClient.getBudgetsByYear(year, ahead));
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @SuppressWarnings("unchecked")
    public List<TaskWorkerConstraintBudget> getAllBudgetsByUser(int year, String userUUID) {
        try {
            return listCache.get("budgets"+year+userUUID, () -> restClient.getBudgetsByYearAndUser(year, userUUID));
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public List<Integer> getCapacityPerMonthByYear(int year) {
        //try { //listCache.get("capacitypermonth"+year, (Callable<? extends List>)
        return new ArrayList<>(Arrays.asList(restClient.getCapacityPerMonthByYear(year)));
        //} catch (ExecutionException e) {
        //  throw new RuntimeException(e.getCause());
        //}
    }

    @SuppressWarnings("unchecked")
    public List<Project> getAllProjects() {
        try {
            return listCache.get("projects", restClient::getProjectsAndTasksAndTaskWorkerConstraints);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Client> getAllClients() {
        try {
            return listCache.get("clients", () -> restClient.getClients());
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Client> getAllClientsGraph() {
        try {
            return listCache.get("clientsgraph", () -> restClient.getClientsGraph());
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Expense> getAllExpensesByYear(int year) {
        try {
            return listCache.get("expenses"+year, () -> restClient.getExpensesByYear(year));
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public Map<String, TaskWorkerConstraint> getTaskWorkerConstraintMap(List<Project> allProjects) {
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

    public Project findProjectByTask(List<Project> allProjects, String taskUUID) {
        for (Project project : allProjects) {
            for (Task task : project.getTasks()) {
                if(task.getUUID().equals(taskUUID)) return project;
            }
        }
        return null;
    }

    public List<Task> findTaskByProject(String projectUUID) {
        for (Project project : getAllProjects()) {
            if (project.getUUID().equals(projectUUID)) return project.getTasks();
        }
        return null;
    }


    public Project findProjectByUUID(String projectUUID) {
        for (Project project : getAllProjects()) {
            if(project.getUUID().equals(projectUUID)) return project;
        }
        return null;
    }

    public Client findClientByUUID(String clientUUID) {
        for (Client client : getAllClients()) {
            if(client.uuid.equals(clientUUID)) return client;
        }
        return null;
    }
}