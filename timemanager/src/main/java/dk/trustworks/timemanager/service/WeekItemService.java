package dk.trustworks.timemanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.timemanager.dto.WeekItem;
import dk.trustworks.timemanager.persistence.WeekItemRepository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by hans on 17/03/15.
 */
public class WeekItemService {

    private WeekItemRepository weekItemRepository;

    public WeekItemService(DataSource ds) {
        weekItemRepository = new WeekItemRepository(ds);
    }

    public List<WeekItem> findAll() {
        return weekItemRepository.findAll();
    }

    public WeekItem findByUUID(String uuid) {
        return weekItemRepository.findByUUID(uuid);
    }

    public List<WeekItem> findByWeekNumberAndYearAndUserUUIDAndTaskUUIDOrderBySortingAsc(int weekNumber, int year, String userUUID, String taskUUID) {
        return weekItemRepository.findByWeekNumberAndYearAndUserUUIDAndTaskUUIDOrderBySortingAsc(weekNumber, year, userUUID, taskUUID);
    }

    public List<WeekItem> findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(int weekNumber, int year, String userUUID) {
        return weekItemRepository.findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(weekNumber, year, userUUID);
    }

    public List<WeekItem> cloneWeek(int weekNumber, int year, String userUUID) {
        return weekItemRepository.findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(weekNumber - 1, year, userUUID);
    }

    public void create(WeekItem weekItem) throws SQLException {
        weekItemRepository.create(weekItem);
    }

    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        weekItemRepository.update(jsonNode, uuid);
    }
}
