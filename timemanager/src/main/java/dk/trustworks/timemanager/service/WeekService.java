package dk.trustworks.timemanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.timemanager.persistence.WeekRepository;

import java.sql.SQLException;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 17/03/15.
 */
public class WeekService extends DefaultLocalService {

    private WeekRepository weekRepository;

    public WeekService() {
        weekRepository = new WeekRepository();
    }

    public List<Map<String, Object>> findByWeekNumberAndYearAndUserUUIDAndTaskUUIDOrderBySortingAsc(Map<String, Deque<String>> queryParameters) {
        int weekNumber = Integer.parseInt(queryParameters.get("weeknumber").getFirst());
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        String userUUID = queryParameters.get("useruuid").getFirst();
        String taskUUID = queryParameters.get("taskuuid").getFirst();
        return weekRepository.findByWeekNumberAndYearAndUserUUIDAndTaskUUIDOrderBySortingAsc(weekNumber, year, userUUID, taskUUID);
    }

    public List<Map<String, Object>> findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(Map<String, Deque<String>> queryParameters) {
        int weekNumber = Integer.parseInt(queryParameters.get("weeknumber").getFirst());
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        String userUUID = queryParameters.get("useruuid").getFirst();
        return weekRepository.findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(weekNumber, year, userUUID);
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        weekRepository.create(jsonNode);
    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        weekRepository.update(jsonNode, uuid);
    }

    @Override
    public GenericRepository getGenericRepository() {
        return weekRepository;
    }

    @Override
    public String getResourcePath() {
        return "weeks";
    }
}
