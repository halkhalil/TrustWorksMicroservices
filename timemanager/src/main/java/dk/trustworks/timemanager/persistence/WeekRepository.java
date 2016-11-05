package dk.trustworks.timemanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.timemanager.dto.Week;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Created by hans on 17/03/15.
 */
public class WeekRepository {

    private final Sql2o sql2o;

    public WeekRepository(DataSource ds) {
        sql2o = new Sql2o(ds);
    }

    public List<Week> findAll() {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM week w ORDER BY sorting ASC")
                    .executeAndFetch(Week.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Week findByUUID(String uuid) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM week w WHERE w.uuid LIKE :uuid ORDER BY sorting ASC")
                    .addParameter("uuid", uuid)
                    .executeAndFetchFirst(Week.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Week> findByWeekNumberAndYearAndUserUUIDAndTaskUUIDOrderBySortingAsc(int weekNumber, int year, String userUUID, String taskUUID) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM week w WHERE w.weeknumber = :weeknumber AND w.year = :year AND w.useruuid " +
                    "LIKE :useruuid AND taskuuid LIKE :taskuuid ORDER BY sorting ASC")
                    .addParameter("weeknumber", weekNumber)
                    .addParameter("year", year)
                    .addParameter("useruuid", userUUID)
                    .addParameter("taskuuid", taskUUID)
                    .executeAndFetch(Week.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Week> findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(int weekNumber, int year, String userUUID) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM week w WHERE w.weeknumber = :weeknumber AND w.year = :year AND w.useruuid " +
                    "LIKE :useruuid ORDER BY sorting ASC")
                    .addParameter("weeknumber", weekNumber)
                    .addParameter("year", year)
                    .addParameter("useruuid", userUUID)
                    .executeAndFetch(Week.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void create(Week week) throws SQLException {
        week.UUID = UUID.randomUUID().toString();
        try (Connection con = sql2o.open()) {
            con.createQuery("INSERT INTO week (uuid, taskuuid, useruuid, weeknumber, year)" +
                    " VALUES (:uuid, :taskuuid, :useruuid, :weeknumber, :year)").bind(week)
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
