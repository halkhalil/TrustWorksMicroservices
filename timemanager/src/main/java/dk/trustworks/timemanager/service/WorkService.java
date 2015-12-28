package dk.trustworks.timemanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.timemanager.persistence.WorkRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 17/03/15.
 */
public class WorkService extends DefaultLocalService {

    private static final Logger log = LogManager.getLogger(WorkService.class);
    private WorkRepository workRepository;

    public WorkService() {
        workRepository = new WorkRepository();
    }

    public List<Map<String, Object>> findByTaskUUID(Map<String, Deque<String>> queryParameters) {
        String taskuuid = queryParameters.get("taskuuid").getFirst();
        return workRepository.findByTaskUUID(taskuuid);
    }

    public List<Map<String, Object>> findByYear(Map<String, Deque<String>> queryParameters) {
        String year = queryParameters.get("year").getFirst();
        return workRepository.findByYear(year);
    }

    public List<Map<String, Object>> findByYearAndUserUUID(Map<String, Deque<String>> queryParameters) {
        String year = queryParameters.get("year").getFirst();
        String userUUID = queryParameters.get("useruuid").getFirst();
        return workRepository.findByYearAndUserUUID(year, userUUID);
    }

    public List<Map<String, Object>> findByYearAndMonth(Map<String, Deque<String>> queryParameters) {
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        int month = Integer.parseInt(queryParameters.get("month").getFirst());
        List<Map<String, Object>> result = workRepository.findByYearAndMonth(year, month);
        log.debug("result: " + result.size());
        return result;
    }

    public List<Map<String, Object>> findByYearAndMonthAndTaskUUIDAndUserUUID(Map<String, Deque<String>> queryParameters) {
        String year = queryParameters.get("year").getFirst();
        String month = queryParameters.get("month").getFirst();
        String taskUUID = queryParameters.get("taskuuid").getFirst();
        String userUUID = queryParameters.get("useruuid").getFirst();
        return workRepository.findByYearAndMonthAndTaskUUIDAndUserUUID(year, month, taskUUID, userUUID);
    }

    public List<Map<String, Object>> findByYearAndMonthAndDayAndTaskUUIDAndUserUUID(Map<String, Deque<String>> queryParameters) {
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        int month = Integer.parseInt(queryParameters.get("month").getFirst());
        int day = Integer.parseInt(queryParameters.get("day").getFirst());
        String taskUUID = queryParameters.get("taskuuid").getFirst();
        String userUUID = queryParameters.get("useruuid").getFirst();
        return workRepository.findByYearAndMonthAndDayAndTaskUUIDAndUserUUID(year, month, day, taskUUID, userUUID);
    }

    public List<Map<String, Object>> findByYearAndMonthAndTaskUUID(Map<String, Deque<String>> queryParameters) {
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        int month = Integer.parseInt(queryParameters.get("month").getFirst());
        String taskUUID = queryParameters.get("taskuuid").getFirst();
        return workRepository.findByYearAndMonthAndTaskUUID(year, month, taskUUID);
    }

    public List<Map<String, Object>> findByYearAndTaskUUIDAndUserUUID(Map<String, Deque<String>> queryParameters) {
        int year = Integer.parseInt(queryParameters.get("year").getFirst());
        String taskUUID = queryParameters.get("taskuuid").getFirst();
        String userUUID = queryParameters.get("useruuid").getFirst();
        return workRepository.findByYearAndTaskUUIDAndUserUUID(year, taskUUID, userUUID);
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        workRepository.create(jsonNode);
    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        workRepository.update(jsonNode, uuid);
    }

    @Override
    public GenericRepository getGenericRepository() {
        return workRepository;
    }

    @Override
    public String getResourcePath() {
        return "works";
    }
}
