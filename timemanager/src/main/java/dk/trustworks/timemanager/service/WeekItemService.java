package dk.trustworks.timemanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.security.Authenticator;
import dk.trustworks.framework.security.RoleRight;
import dk.trustworks.framework.model.WeekItem;
import dk.trustworks.timemanager.persistence.WeekItemRepository;
import net.sf.cglib.proxy.Enhancer;

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

    public WeekItemService() {
    }

    public static WeekItemService getInstance(DataSource ds) {
        WeekItemService service = new WeekItemService(ds);
        return (WeekItemService) Enhancer.create(service.getClass(), new Authenticator(service));
    }

    @RoleRight("tm.user")
    public List<WeekItem> findAll() {
        return weekItemRepository.findAll();
    }

    @RoleRight("tm.user")
    public WeekItem findByUUID(String uuid) {
        return weekItemRepository.findByUUID(uuid);
    }

    @RoleRight("tm.user")
    public List<WeekItem> findByWeekNumberAndYearAndUserUUIDAndTaskUUIDOrderBySortingAsc(int weekNumber, int year, String userUUID, String taskUUID) {
        return weekItemRepository.findByWeekNumberAndYearAndUserUUIDAndTaskUUIDOrderBySortingAsc(weekNumber, year, userUUID, taskUUID);
    }

    @RoleRight("tm.user")
    public List<WeekItem> findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(int weekNumber, int year, String userUUID) {
        return weekItemRepository.findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(weekNumber, year, userUUID);
    }

    @RoleRight("tm.user")
    public List<WeekItem> cloneWeek(int weekNumber, int year, String userUUID) {
        return weekItemRepository.findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(weekNumber - 1, year, userUUID);
    }

    @RoleRight("tm.user")
    public void create(WeekItem weekItem) throws SQLException {
        weekItemRepository.create(weekItem);
    }

    @RoleRight("tm.user")
    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        weekItemRepository.update(jsonNode, uuid);
    }
}
