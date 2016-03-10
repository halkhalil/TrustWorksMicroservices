package dk.trustworks.timemanager.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.framework.network.Locator;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.timemanager.dto.Client;
import dk.trustworks.timemanager.dto.Project;
import dk.trustworks.timemanager.dto.Task;
import dk.trustworks.timemanager.dto.TaskWorkerConstraint;
import dk.trustworks.timemanager.persistence.TaskWeekViewRepository;
import dk.trustworks.timemanager.persistence.WeekRepository;
import dk.trustworks.timemanager.persistence.WorkRepository;
import io.undertow.server.handlers.cache.CacheHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.io.IOException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.StreamSupport;

import static java.util.Calendar.*;

/**
 * Created by hans on 15/05/15.
 */
public class TaskWeekViewService extends DefaultLocalService {

    private static final Logger log = LogManager.getLogger(TaskWeekViewService.class);
    private TaskWeekViewRepository taskWeekViewRepository;

    public TaskWeekViewService() {
        taskWeekViewRepository = new TaskWeekViewRepository();
    }

    public List<Client> getAllClients() {
        HttpResponse<com.mashape.unirest.http.JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/clients")
                    .queryString("children", "projectuuid/taskuuid/taskworkerconstraintuuid")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Client>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
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

    private Task getTask(String taskUUID, List<Client> clients) {
        for (Client client : clients) {
            for (Project project : client.projects) {
                for (Task task : project.getTasks()) {
                    if(task.getUUID().equals(taskUUID)) return task;
                }
            }
        }
        return null;
    }

    private Project getProject(String projectUUID, List<Client> clients) {
        for (Client client : clients) {
            for (Project project : client.projects) {
                if(project.getUUID().equals(projectUUID)) return project;
            }
        }
        return null;
    }

    public List<Map<String, Object>> findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(Map<String, Deque<String>> queryParameters) {
        int weekNumber = Integer.parseInt(queryParameters.get("weeknumber").getFirst());
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        String userUUID = queryParameters.get("useruuid").getFirst();

        WeekRepository weekRepository = new WeekRepository();
        WorkRepository workRepository = new WorkRepository();
        List<Client> clients = getAllClients();
        List<Map<String, Object>> taskWeekViews = new ArrayList<>();
        List<Map<String, Object>> weeks = weekRepository.findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(weekNumber, year, userUUID);

        StreamSupport.stream(weeks.spliterator(), true).map((week) -> {
            Object taskUUID = week.get("taskuuid");
            Task task = getTask(taskUUID.toString(), clients);
            Map<String, Object> taskWeekView = new HashMap<>();
            Project project = getProject(task.getProjectUUID(), clients);
            Map<String, Object> client = new ClientService().getOneEntity("clients", project.getClientUUID().toString());
            taskWeekView.put("taskname", task.getName() + " / " + project.getName() + " / " + client.get("name"));
            taskWeekView.put("taskuuid", taskUUID.toString());
            taskWeekView.put("sorting", week.get("sorting"));

            double budgetLeft = 0.0;
            try {
                HttpResponse<com.mashape.unirest.http.JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/taskbudgets/search/findByTaskUUIDAndUserUUID")
                        .queryString("taskuuid", taskUUID)
                        .queryString("useruuid", userUUID)
                        .header("accept", "application/json")
                        .asJson();
                budgetLeft = (double) jsonResponse.getBody().getObject().get("remaining");
            } catch (UnirestException e) {
                log.error("LOG00780:", e);
            }

            taskWeekView.put("budgetleft", budgetLeft);

            DateTime dateTime = new DateTime();
            dateTime = dateTime.withYear(year).withDayOfWeek(1).withWeekOfWeekyear(weekNumber);

            for (int i = 0; i < 7; i++) {
                List<Map<String, Object>> works = workRepository.findByYearAndMonthAndDayAndTaskUUIDAndUserUUID(dateTime.getYear(), dateTime.getMonthOfYear() - 1, dateTime.getDayOfMonth(), taskUUID.toString(), userUUID);
                for (Map<String, Object> work : works) {
                    taskWeekView.put(DayOfWeek.of(i + 1).getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase(), work.get("workduration"));
                }
                dateTime = dateTime.plusDays(1);
            }
            return taskWeekView;
        }).forEach(result -> taskWeekViews.add(result));
        taskWeekViews.sort((p1, p2) -> Integer.compare(Integer.parseInt(p1.get("sorting").toString()), Integer.parseInt(p2.get("sorting").toString())));

        return taskWeekViews;
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        taskWeekViewRepository.create(jsonNode);
    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        taskWeekViewRepository.update(jsonNode, uuid);
    }

    @Override
    public GenericRepository getGenericRepository() {
        return taskWeekViewRepository;
    }

    @Override
    public String getResourcePath() {
        return "taskweekviews";
    }
}
