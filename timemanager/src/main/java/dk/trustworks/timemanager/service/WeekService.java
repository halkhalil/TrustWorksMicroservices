package dk.trustworks.timemanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.timemanager.dto.Week;
import dk.trustworks.timemanager.persistence.WeekRepository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by hans on 17/03/15.
 */
public class WeekService {

    private WeekRepository weekRepository;

    public WeekService(DataSource ds) {
        weekRepository = new WeekRepository(ds);
    }

    public List<Week> findAll() {
        return weekRepository.findAll();
    }

    public Week findByUUID(String uuid) {
        return weekRepository.findByUUID(uuid);
    }

    public List<Week> findByWeekNumberAndYearAndUserUUIDAndTaskUUIDOrderBySortingAsc(int weekNumber, int year, String userUUID, String taskUUID) {
        return weekRepository.findByWeekNumberAndYearAndUserUUIDAndTaskUUIDOrderBySortingAsc(weekNumber, year, userUUID, taskUUID);
    }

    public List<Week> findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(int weekNumber, int year, String userUUID) {
        return weekRepository.findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(weekNumber, year, userUUID);
    }

    public List<Week> cloneWeek(int weekNumber, int year, String userUUID) {
        return weekRepository.findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(weekNumber - 1, year, userUUID);
    }

    public void create(Week week) throws SQLException {
        weekRepository.create(week);
    }

    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        weekRepository.update(jsonNode, uuid);
    }
}
