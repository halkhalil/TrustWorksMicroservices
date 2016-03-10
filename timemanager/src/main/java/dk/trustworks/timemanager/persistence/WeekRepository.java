package dk.trustworks.timemanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.persistence.GenericRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hans on 17/03/15.
 */
public class WeekRepository extends GenericRepository {

    private static final Logger log = LogManager.getLogger(WeekRepository.class);

    public WeekRepository() {
        super();
    }

    public List<Map<String, Object>> findByWeekNumberAndYearAndUserUUIDAndTaskUUIDOrderBySortingAsc(int weekNumber, int year, String userUUID, String taskUUID) {
        log.debug("WeekRepository.findByWeekNumberAndYearAndUserUUIDAndTaskUUIDOrderBySortingAsc");
        log.debug("weekNumber = [" + weekNumber + "], year = [" + year + "], userUUID = [" + userUUID + "], taskUUID = [" + taskUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM week w WHERE w.weeknumber = :weeknumber AND w.year = :year AND w.useruuid LIKE :useruuid AND taskuuid LIKE :taskuuid ORDER BY sorting ASC")
                    .addParameter("weeknumber", weekNumber)
                    .addParameter("year", year)
                    .addParameter("useruuid", userUUID)
                    .addParameter("taskuuid", taskUUID)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00570:", e);
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, Object>> findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(int weekNumber, int year, String userUUID) {
        log.debug("WeekRepository.findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc");
        log.debug("weekNumber = [" + weekNumber + "], year = [" + year + "], userUUID = [" + userUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            List<Map<String, Object>> result = getEntitiesFromMapSet(con.createQuery("SELECT * FROM week w WHERE w.weeknumber = :weeknumber AND w.year = :year AND w.useruuid LIKE :useruuid ORDER BY sorting ASC")
                    .addParameter("weeknumber", weekNumber)
                    .addParameter("year", year)
                    .addParameter("useruuid", userUUID)
                    .executeAndFetchTable().asList());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("LOG00580:", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        log.debug("WeekRepository.create");
        log.debug("jsonNode = [" + jsonNode + "]");
        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("INSERT INTO week (uuid, taskuuid, useruuid, weeknumber, year)" +
                    " VALUES (:uuid, :taskuuid, :useruuid, :weeknumber, :year)")
                    .addParameter("uuid", UUID.randomUUID().toString())
                    .addParameter("taskuuid", jsonNode.get("taskuuid").asText())
                    .addParameter("useruuid", jsonNode.get("useruuid").asText())
                    .addParameter("weeknumber", jsonNode.get("weeknumber").asInt())
                    .addParameter("year", jsonNode.get("year").asInt())
                    .executeUpdate();
        } catch (Exception e) {
            log.error("LOG00600:", e);
        }
    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {

    }
}
