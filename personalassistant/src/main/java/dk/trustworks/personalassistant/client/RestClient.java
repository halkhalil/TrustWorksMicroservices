package dk.trustworks.personalassistant.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.framework.model.*;
import dk.trustworks.personalassistant.client.timemanager.Locator;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 18/05/15.
 */
public class RestClient {

    public List<Work> getRegisteredWorkByYearMonthDay(int year, int month, int day) {
        System.out.println("RestClient.getRegisteredWorkByYearMonthDay");
        System.out.println("year = [" + year + "], month = [" + month + "], day = [" + day + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice") + "/api/works/search/findByYearAndMonthAndDay")
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

    public List<TaskWorkerConstraintBudget> getBudgetsByMonthAndYear(int month, int year) {
        System.out.println("RestClient.getBudgetsByMonthAndYear");
        System.out.println("month = [" + month + "], year = [" + year + "]");
        LocalDate periodStart = new LocalDate(year, month, 1);
        LocalDate periodEnd = periodStart.plusMonths(1);
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/budget/search/findByPeriod")
                    .queryString("periodStart", periodStart)
                    .queryString("periodEnd", periodEnd)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<TaskWorkerConstraintBudget> taskBudgets = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<TaskWorkerConstraintBudget>>() {});
            return taskBudgets;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public List<Project> getProjectsAndTasksAndTaskWorkerConstraints() {
        System.out.println("RestClient.getProjectsAndTasksAndTaskWorkerConstraints");
        try {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/projects")
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

    public List<Work> getRegisteredWorkByMonth(int year, int month) {
        System.out.println("RestClient.getRegisteredWorkByMonth");
        System.out.println("month = [" + month + "], year = [" + year + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice") + "/api/works/search/findByYearAndMonth")
                    .queryString("month", month)
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<Work> result = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Work>>() {
            });
            return result;
        } catch (UnirestException | IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<User> getUsers() {
        System.out.println("RestClient.getUsers");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/users")
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
        System.out.println("RestClient.getUserAvailabilities");
        System.out.println("useruuid = [" + useruuid + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/users/"+useruuid+"/capacities")
                    .header("accept", "application/json")
                    .queryString("periodStart", periodStart)
                    .queryString("periodEnd", periodEnd)
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JodaModule());
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Capacity>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
