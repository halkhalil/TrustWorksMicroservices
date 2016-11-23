package dk.trustworks.timemanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.timemanager.dto.WeekItem;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Created by hans on 17/03/15.
 */
public class WeekItemRepository {

    private final Sql2o sql2o;

    public WeekItemRepository(DataSource ds) {
        sql2o = new Sql2o(ds);
    }

    public List<WeekItem> findAll() {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM week w ORDER BY sorting ASC")
                    .executeAndFetch(WeekItem.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public WeekItem findByUUID(String uuid) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM week w WHERE w.uuid LIKE :uuid ORDER BY sorting ASC")
                    .addParameter("uuid", uuid)
                    .executeAndFetchFirst(WeekItem.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<WeekItem> findByWeekNumberAndYearAndUserUUIDAndTaskUUIDOrderBySortingAsc(int weekNumber, int year, String userUUID, String taskUUID) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM week w WHERE w.weeknumber = :weeknumber AND w.year = :year AND w.useruuid " +
                    "LIKE :useruuid AND taskuuid LIKE :taskuuid ORDER BY sorting ASC")
                    .addParameter("weeknumber", weekNumber)
                    .addParameter("year", year)
                    .addParameter("useruuid", userUUID)
                    .addParameter("taskuuid", taskUUID)
                    .executeAndFetch(WeekItem.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<WeekItem> findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(int weekNumber, int year, String userUUID) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM week w WHERE w.weeknumber = :weeknumber AND w.year = :year AND w.useruuid " +
                    "LIKE :useruuid ORDER BY sorting ASC")
                    .addParameter("weeknumber", weekNumber)
                    .addParameter("year", year)
                    .addParameter("useruuid", userUUID)
                    .executeAndFetch(WeekItem.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void create(WeekItem weekItem) throws SQLException {
        weekItem.uuid = UUID.randomUUID().toString();
        try (Connection con = sql2o.open()) {
            con.createQuery("INSERT INTO week (uuid, taskuuid, useruuid, weeknumber, year)" +
                    " VALUES (:uuid, :taskuuid, :useruuid, :weeknumber, :year)").bind(weekItem)
                    /*
                    .addParameter("uuid", UUID.randomUUID().toString())
                    .addParameter("taskuuid", jsonNode.get("taskuuid").asText())
                    .addParameter("useruuid", jsonNode.get("useruuid").asText())
                    .addParameter("weeknumber", jsonNode.get("weeknumber").asInt())
                    .addParameter("year", jsonNode.get("year").asInt())
                    */
                    .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(JsonNode jsonNode, String uuid) throws SQLException {

    }
}
