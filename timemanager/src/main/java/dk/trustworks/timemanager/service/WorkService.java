package dk.trustworks.timemanager.service;

import dk.trustworks.timemanager.dto.Work;
import dk.trustworks.timemanager.persistence.WorkRepository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 17/03/15.
 */
public class WorkService {

    private WorkRepository workRepository;

    public WorkService(DataSource ds) {
        workRepository = new WorkRepository(ds);
    }

    public List<Work> findByTaskUUID(String taskUUID) {
        return workRepository.findByTaskUUID(taskUUID);
    }

    public List<Work> findByYear(int year) {
        return workRepository.findByYear(year);
    }

    public List<Work> findByYearAndUserUUID(String userUUID, int year) {
        return workRepository.findByYearAndUserUUID(year, userUUID);
    }

    public List<Work> findByYearAndMonth(int year, int month) {
        return workRepository.findByYearAndMonth(year, month);
    }

    public List<Work> findByYearAndMonthAndTaskUUIDAndUserUUID(int year, int month, String taskUUID, String userUUID) {
        return workRepository.findByYearAndMonthAndTaskUUIDAndUserUUID(year, month, taskUUID, userUUID);
    }

    public List<Work> findByYearAndMonthAndDayAndTaskUUIDAndUserUUID(int year, int month, int day, String taskUUID, String userUUID) {
        return workRepository.findByYearAndMonthAndDayAndTaskUUIDAndUserUUID(year, month, day, taskUUID, userUUID);
    }

    public List<Work> findByYearAndMonthAndDay(int year, int month, int day) {
        return workRepository.findByYearAndMonthAndDay(year, month, day);
    }

    public List<Work> findByYearAndMonthAndTaskUUID(int year, int month, String taskUUID) {
        return workRepository.findByYearAndMonthAndTaskUUID(year, month, taskUUID);
    }

    public List<Work> findByYearAndTaskUUIDAndUserUUID(int year, String taskUUID, String userUUID) {
        return workRepository.findByYearAndTaskUUIDAndUserUUID(year, taskUUID, userUUID);
    }

    public Map<String, Object> calculateTaskUserTotalDuration(String taskUUID, String userUUID) {
        Map<String, Object> result = new HashMap<>();
        result.put("totalworkduration", workRepository.calculateTaskUserTotalDuration(taskUUID, userUUID));
        return result;
    }

    public void create(Work work) throws SQLException {
        workRepository.create(work);
    }

    public void update(Work work, String uuid) throws SQLException {
        workRepository.update(work, uuid);
    }
}
