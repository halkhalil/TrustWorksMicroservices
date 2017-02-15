package dk.trustworks.client.timemanager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.framework.model.*;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 18/05/15.
 */
public class RestClient {

    static final Logger log = Logger.getLogger(RestClient.class);

    private final String timeUrl = "http://ws.trustworks.dk/timeservice/api";
    private final String clientUrl = "http://ws.trustworks.dk/clientservice/api";
    private final String usersUrl = "http://ws.trustworks.dk/userservice/api";


/*
    private final String timeUrl = "http://localhost:9093/api";
    private final String clientUrl = "http://localhost:9092/api";
    private final String usersUrl = "http://localhost:9091/api";
*/

    public List<TaskWorkerConstraintBudget> getBudgetsByMonthAndYear(int month, int year) {
        log.debug("RestClient.getBudgetsByMonthAndYear");
        log.debug("month = [" + month + "], year = [" + year + "]");
        LocalDate periodStart = new LocalDate(year, month, 1);
        LocalDate periodEnd = periodStart.plusMonths(1);
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(clientUrl + "/budget/search/findByPeriod")
                    .queryString("periodStart", periodStart)
                    .queryString("periodEnd", periodEnd)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<TaskWorkerConstraintBudget> taskBudgets = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<TaskWorkerConstraintBudget>>() {});
            return taskBudgets;
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public List<Project> getProjectsAndTasksAndTaskWorkerConstraints() {
        log.debug("RestClient.getProjectsAndTasksAndTaskWorkerConstraints");
        try {
            // http://ws.trustworks.dk/clientservice/api/api/projects?projection=task/taskworkerconstraint
            HttpResponse<JsonNode> jsonResponse = Unirest.get(clientUrl + "/projects")
                    .queryString("projection", "task/taskworkerconstraint")
                    .header("accept", "application/json")
                    .asJson();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Project>>() {});
        } catch (UnirestException | IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<User> getUsers() {
        log.debug("RestClient.getUsers");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(usersUrl + "/users/v2")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<User>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Capacity> getUserCapacities(String useruuid, LocalDate periodStart, LocalDate periodEnd) {
        log.debug("RestClient.getUserAvailabilities");
        log.debug("useruuid = [" + useruuid + "], periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(usersUrl + "/users/"+useruuid+"/capacities")
                    .header("accept", "application/json")
                    .queryString("periodStart", periodStart)
                    .queryString("periodEnd", periodEnd)
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JodaModule());
            mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Capacity>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Work> getRegisteredWorkByYearMonthDay(int year, int month, int day) {
        log.debug("RestClient.getRegisteredWorkByYearMonthDay");
        log.debug("year = [" + year + "], month = [" + month + "], day = [" + day + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(timeUrl + "/works/search/findByYearAndMonthAndDay")
                    .queryString("month", month)
                    .queryString("year", year)
                    .queryString("day", day)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<Work> result = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Work>>() {});
            return result;
        } catch (UnirestException | IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Work> getRegisteredWorkByMonth(int year, int month) {
        log.debug("RestClient.getRegisteredWorkByMonth");
        log.debug("month = [" + month + "], year = [" + year + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(timeUrl + "/works/search/findByYearAndMonth")
                    .queryString("month", month)
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<Work> result = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Work>>() {
            });
            return result;
        } catch (UnirestException | IOException e) {
            log.error(e);
        }

        return new ArrayList<>();
    }

    public TaskWorkerConstraint getTaskWorkerConstraint(String taskUUID, String userUUID) {
        log.debug("RestClient.getTaskWorkerConstraint");
        log.debug("taskUUID = [" + taskUUID + "], userUUID = [" + userUUID + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(clientUrl + "/taskuserprice/search/findByTaskUUIDAndUserUUID")
                    .queryString("taskuuid", taskUUID)
                    .queryString("useruuid", userUUID)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            TaskWorkerConstraint result = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<TaskWorkerConstraint>() {});
            return result;
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public void postTaskBudget(TaskWorkerConstraintBudget taskWorkerConstraintBudget) {
        log.debug("RestClient.postTaskBudget");
        log.debug("taskWorkerConstraintBudget = [" + taskWorkerConstraintBudget + "]");
        try {
            System.out.println("new ObjectMapper().writeValueAsString(taskWorkerConstraintBudget) = " + new ObjectMapper().writeValueAsString(taskWorkerConstraintBudget));
            Unirest.post(clientUrl + "/budget")
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(new ObjectMapper().writeValueAsString(taskWorkerConstraintBudget))
                    .asString();
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException("Kunne ikke skrive: taskWorkerConstraintBudget " + taskWorkerConstraintBudget, e);
        }
    }
}
