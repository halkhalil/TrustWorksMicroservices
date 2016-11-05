package dk.trustworks.bimanager.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.bimanager.client.commands.*;
import dk.trustworks.bimanager.dto.*;
import dk.trustworks.framework.network.Locator;
import dk.trustworks.framework.server.DefaultHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 18/05/15.
 */
public class RestClient {

    private static final Logger log = LogManager.getLogger(RestClient.class);


    public double getTaskUserWorkHours(String taskuuid, String useruuid) {
        log.entry(taskuuid, useruuid);
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice") + "/api/works/calculateworkduration")
                    .queryString("taskuuid", taskuuid)
                    .queryString("useruuid", useruuid)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            log.exit((double) jsonResponse.getBody().getObject().get("totalworkduration"));
            return (double) jsonResponse.getBody().getObject().get("totalworkduration");
        } catch (UnirestException e) {
            log.catching(e);
        }
        log.exit(0.0);
        return 0.0;
    }

    public double getProjectBudgetByTask(String taskUUID) {
        log.entry(taskUUID);
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/tasks/" + taskUUID)
                    .queryString("projection", "projectuuid")
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            log.exit((double) jsonResponse.getBody().getObject().getJSONObject("project").get("budget"));
            return (double) jsonResponse.getBody().getObject().getJSONObject("project").get("budget");
        } catch (UnirestException e) {
            log.catching(e);
        }
        log.exit(0.0);
        return 0.0;
    }

    public double getTaskWorkerRate(String taskuuid, String useruuid) {
        log.debug("RestClient.getTaskWorkerRate");
        log.debug("taskuuid = [" + taskuuid + "], useruuid = [" + useruuid + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraints/search/findByTaskUUIDAndUserUUID")
                    .queryString("taskuuid", taskuuid)
                    .queryString("useruuid", useruuid)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            return (double) jsonResponse.getBody().getObject().get("price");
        } catch (UnirestException e) {
            log.catching(e);
        }
        return 0.0;
    }

    public List<Work> getRegisteredWorkByMonth(int year, int month) {
        log.entry(month, year);
        System.out.println("RestClient.getRegisteredWorkByMonth");
        System.out.println("month = [" + month + "], year = [" + year + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice") + "/api/works/search/findByYearAndMonth")
                    .queryString("month", month)
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<Work> result = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Work>>() {
            });
            log.exit(result);
            log.debug("result: " + result.size());
            return result;
        } catch (UnirestException | IOException e) {
            log.catching(e);
        }
        log.exit(new ArrayList<>());
        return new ArrayList<>();
    }

    public List<Work> getRegisteredWorkByYear(int year) {
        log.entry(year);
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice") + "/api/works/search/findByYear")
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<Work> result = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Work>>() {});
            log.exit(result);
            return result;
        } catch (UnirestException | IOException e) {
            e.printStackTrace();
        }
        log.exit(new ArrayList<>());
        return new ArrayList<>();
    }

    public List<Capacity> getCapacityPerMonthByYear(LocalDate periodStart, LocalDate periodEnd) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/capacities")
                    .queryString("periodStart", periodStart)
                    .queryString("periodEnd", periodEnd)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            JodaModule module = new JodaModule();
            mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);
            mapper.registerModule(module);

            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Capacity>>() {});
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("NOTHING FOUND!!!");
        return new ArrayList<>();
    }

    public List<Capacity> getCapacityPerMonthByYearByUser(LocalDate periodStart, LocalDate periodEnd, String userUUID) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/users/"+userUUID+"/capacities")
                    .queryString("periodStart", periodStart)
                    .queryString("periodEnd", periodEnd)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Capacity>>() {});
            /*
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("capacitypermonthbyuser");
            int[] result = new int[12];
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i=0;i<len;i++){
                    result[i] = jsonArray.getInt(i);
                }
            }
            return result;
            */
        } catch (UnirestException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (JsonParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<Work> getRegisteredWorkByUserAndYear(String userUUID, int year) {
        log.entry(userUUID, year);
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice") + "/api/works/search/findByYearAndUserUUID")
                    .queryString("useruuid", userUUID)
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<Work> result = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Work>>() {
            });
            log.exit(result);
            return result;
        } catch (UnirestException | IOException e) {
            log.catching(e);
        }
        log.exit(new ArrayList<>());
        return new ArrayList<>();
    }

    public TaskWorkerConstraint getTaskWorkerConstraint(String taskUUID, String userUUID) {
        log.entry(taskUUID, userUUID);
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraints/search/findByTaskUUIDAndUserUUID")
                    .queryString("taskuuid", taskUUID)
                    .queryString("useruuid", userUUID)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            TaskWorkerConstraint result = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<TaskWorkerConstraint>() {
            });
            log.exit(result);
            return result;
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public List<TaskWorkerConstraint> getTaskWorkerConstraint(String taskUUID) {
        log.debug("RestClient.getTaskWorkerConstraint");
        log.debug("taskUUID = [" + taskUUID + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraints/search/findByTaskUUID")
                    .queryString("taskuuid", taskUUID)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<TaskWorkerConstraint>>() {
            });
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public List<TaskWorkerConstraintBudget> getBudgetsByTaskWorkerConstraintUUID(TaskWorkerConstraint taskWorkerConstraint) {
        log.debug("RestClient.getBudgetsByTaskWorkerConstraintUUID");
        log.debug("taskWorkerConstraint = [" + taskWorkerConstraint + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraintbudgets/search/findByTaskWorkerConstraintUUID")
                    .queryString("taskworkerconstraintuuid", taskWorkerConstraint.getUUID())
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<TaskWorkerConstraintBudget> taskBudgets = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<TaskWorkerConstraintBudget>>() {
            });
            return taskBudgets;
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public List<TaskWorkerConstraintBudget> getBudgetsByMonthAndYear(int month, int year) {
        log.debug("RestClient.getBudgetsByMonthAndYear");
        log.debug("month = [" + month + "], year = [" + year + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraintbudgets/search/findByMonthAndYear")
                    .queryString("month", month)
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<TaskWorkerConstraintBudget> taskBudgets = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<TaskWorkerConstraintBudget>>() {
            });
            return taskBudgets;
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public List<TaskWorkerConstraintBudget> getBudgetsByYear(int year, int ahead) {
        log.debug("RestClient.getBudgetsByYear");
        log.debug("year = [" + year + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraintbudgets/search/findByYear")
                    .queryString("year", year)
                    .queryString("ahead", ahead)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<TaskWorkerConstraintBudget> taskBudgets = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<TaskWorkerConstraintBudget>>() {
            });
            return taskBudgets;
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraintBudget", e);
        }
    }

    public List<TaskWorkerConstraintBudget> getBudgetsByYearAndUser(int year, String userUUID) {
        log.debug("RestClient.getBudgetsByYearAndUser");
        log.debug("year = [" + year + "]");
        log.debug("userUUID = [" + userUUID + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraintbudgets/search/findByYearAndUser")
                    .queryString("year", year)
                    .queryString("useruuid", userUUID)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<TaskWorkerConstraintBudget> taskBudgets = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<TaskWorkerConstraintBudget>>() {
            });
            return taskBudgets;
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraintBudget", e);
        }
    }

    public List<TaskWorkerConstraintBudget> getBudgetsByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(TaskWorkerConstraint taskWorkerConstraint, int month, int year, Long datetime) {
        log.debug("RestClient.getBudgetsByTaskWorkerConstraintUUIDAndMonthAndYearAndDate");
        log.debug("taskWorkerConstraint = [" + taskWorkerConstraint + "], month = [" + month + "], year = [" + year + "], datetime = [" + datetime + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraintbudgets/search/findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate")
                    .queryString("taskworkerconstraintuuid", taskWorkerConstraint.getUUID())
                    .queryString("month", month)
                    .queryString("year", year)
                    .queryString("datetime", datetime)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<TaskWorkerConstraintBudget> taskBudgets = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<TaskWorkerConstraintBudget>>() {
            });
            return taskBudgets;
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: TaskWorkerConstraint", e);
        }
    }

    public List<ProjectYearEconomy> getProjectBudgetsByYear(int year) {
        log.debug("RestClient.getProjectBudgetsByYear");
        log.debug("year = [" + year + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/projectbudgets/search/findByYear")
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<ProjectYearEconomy> projectBudgets = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<ProjectYearEconomy>>() {
            });
            return projectBudgets;
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: economyByMonth", e);
        }
    }

    public List<ProjectYearEconomy> getProjectBudgetsByUserAndYear(String userUUID, int year) {
        log.debug("RestClient.getProjectBudgetsByYear");
        log.debug("userUUID = [" + userUUID + "], year = [" + year + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/projectbudgets/search/findByUserAndYear")
                    .queryString("useruuid", userUUID)
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<ProjectYearEconomy> projectBudgets = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<ProjectYearEconomy>>() {
            });
            return projectBudgets;
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: economyByMonth", e);
        }
    }

    public List<ProjectYearEconomy> getProjectBudgetsByUserAndYearAndHours(String userUUID, int year) {
        log.debug("RestClient.getProjectBudgetsByYear");
        log.debug("userUUID = [" + userUUID + "], year = [" + year + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/projectbudgets/search/findByUserAndYearAndHours")
                    .queryString("useruuid", userUUID)
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<ProjectYearEconomy> projectBudgets = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<ProjectYearEconomy>>() {
            });
            return projectBudgets;
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: economyByMonth", e);
        }
    }

    public Map<String, String> getTaskProjectClient(String taskUUID) {
        log.debug("RestClient.getTaskProjectClient");
        log.debug("taskUUID = [" + taskUUID + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/tasks/" + taskUUID)
                    .queryString("projection", "projectuuid/clientuuid")
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            log.debug("getTaskWorkerRate: jsonResponse.getBody().getObject().toString() = " + jsonResponse.getBody().getObject().toString());

            HashMap<String, String> result = new HashMap<>();
            result.put("taskname", jsonResponse.getBody().getObject().get("name").toString());
            result.put("projectname", jsonResponse.getBody().getObject().getJSONObject("project").get("name").toString());
            result.put("clientname", jsonResponse.getBody().getObject().getJSONObject("project").getJSONObject("client").get("name").toString());
            return result;
        } catch (UnirestException e) {
            log.catching(e);
        }
        log.info("LOG00080: Returning null from getTaskProjectClient");
        return null;
    }

    public Project getProjectByUUID(String projectUUID) {
        log.debug("RestClient.getProjectByUUID");
        log.debug("projectUUID = [" + projectUUID + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/projects/" + projectUUID)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            log.debug("jsonResponse = " + jsonResponse.getBody());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<Project>() {
            });
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: project " + projectUUID, e);
        }
    }

    public Task getTaskByUUID(String taskUUID) {
        log.debug("RestClient.getTaskByUUID");
        log.debug("taskUUID = [" + taskUUID + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/tasks/" + taskUUID)
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<Task>() {
            });
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: task " + taskUUID, e);
        }
    }

    public List<Task> getAllProjectTasks(String projectUUID) {
        log.debug("RestClient.getAllProjectTasks");
        log.debug("projectUUID = [" + projectUUID + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/tasks/search/findByProjectUUIDOrderByNameAsc")
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .queryString("projectuuid", projectUUID)
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Task>>() {
            });
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: task by projectuuid " + projectUUID, e);
        }
    }

    public List<Project> getProjects() {
        log.debug("RestClient.getProjects");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/projects")
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Project>>() {
            });
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke loade: projects ", e);
        }
    }

    public List<Client> getClients() {
        return new GetClientsCommand().execute();
    }

    public List<Client> getClientsGraph() { return new GetClientsGraphCommand().execute(); }

    public List<Project> getProjectsAndTasksAndTaskWorkerConstraints() {
        return new GetProjectsAndTasksAndTaskWorkerConstraintsCommand().execute();
    }

    public List<User> getUsers() {
        try {
            return new GetUsersCommand().run();//;.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void postTaskBudget(TaskWorkerConstraintBudget taskWorkerConstraintBudget) {
        log.debug("RestClient.postTaskBudget");
        log.debug("taskWorkerConstraintBudget = [" + taskWorkerConstraintBudget + "]");
        try {
            Unirest.post(Locator.getInstance().resolveURL("clientservice") + "/api/taskworkerconstraintbudgets")
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .body(new ObjectMapper().writeValueAsString(taskWorkerConstraintBudget))
                    .asJson();
        } catch (Exception e) {
            log.throwing(e);
            throw new RuntimeException("Kunne ikke skrive: taskWorkerConstraintBudget " + taskWorkerConstraintBudget, e);
        }
    }

    public List<Expense> getExpenses() {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("financeservice") + "/api/expenses")
                    .header("accept", "application/json")
                    .header("jwt-token", DefaultHandler.JWTTOKEN.get())
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Expense>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Expense> getExpensesByYear(int year) {
        return new GetExpensesByYearCommand(year).execute();
    }
}
