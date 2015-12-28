package dk.trustworks.timemanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.framework.network.Locator;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.timemanager.persistence.TaskWeekViewRepository;
import dk.trustworks.timemanager.persistence.WeekRepository;
import dk.trustworks.timemanager.persistence.WorkRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

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

    public List<Map<String, Object>> findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(Map<String, Deque<String>> queryParameters) {
        int weekNumber = Integer.parseInt(queryParameters.get("weeknumber").getFirst());
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        String userUUID = queryParameters.get("useruuid").getFirst();

        WeekRepository weekRepository = new WeekRepository();
        WorkRepository workRepository = new WorkRepository();
        List<Map<String, Object>> taskWeekViews = new ArrayList<>();
        List<Map<String, Object>> weeks = weekRepository.findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(weekNumber, year, userUUID);

        StreamSupport.stream(weeks.spliterator(), true).map((week) -> {
            Object taskUUID = week.get("taskuuid");
            Map<String, Object> task = new TaskService().getOneEntity("tasks", taskUUID.toString());
            Map<String, Object> taskWeekView = new HashMap<>();
            Map<String, Object> project = new ProjectService().getOneEntity("projects", task.get("projectuuid").toString());
            Map<String, Object> client = new ClientService().getOneEntity("clients", project.get("clientuuid").toString());
            taskWeekView.put("taskname", task.get("name") + " / " + project.get("name") + " / " + client.get("name"));
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
            System.out.println(year + ", " + weekNumber);


            Calendar c = getInstance();
            c.setFirstDayOfWeek(MONDAY);
            c.clear();
            c.set(YEAR, year);
            c.set(WEEK_OF_YEAR, weekNumber);
            System.out.println(c.get(YEAR) + ", " + c.get(MONTH) + ", " + c.get(DAY_OF_MONTH));


            DateTime dateTime = new DateTime();
            dateTime = dateTime.withYear(year).withDayOfWeek(1).withWeekOfWeekyear(weekNumber);

            System.out.println(dateTime.getYear() + ", " + dateTime.getMonthOfYear() + ", " + dateTime.getDayOfMonth());

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
