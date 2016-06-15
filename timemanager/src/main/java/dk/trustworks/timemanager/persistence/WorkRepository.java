package dk.trustworks.timemanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.persistence.GenericRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hans on 17/03/15.
 */
public class WorkRepository extends GenericRepository {

    private static final Logger log = LogManager.getLogger(WorkRepository.class);

    //Cache<String, Double> calculateTaskUserTotalDurationCache = CacheBuilder.newBuilder().maximumSize(100).build();

    public WorkRepository() {
        super();
    }

    public List<Map<String, Object>> findByTaskUUID(String taskUUID) {
        log.debug("WorkRepository.findByTaskUUID");
        log.debug("taskuuid = [" + taskUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("select yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "from work yt inner join( " +
                    "select uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "from work WHERE taskuuid LIKE :taskuuid " +
                    "group by day, month, year " +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.day = ss.day and yt.created = ss.created and yt.taskuuid = ss.taskuuid and yt.useruuid = ss.useruuid;")
                    .addParameter("taskuuid", taskUUID)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00610:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByYear(String year) {
        log.debug("WorkRepository.findByYear");
        log.debug("year = [" + year + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("select yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "from work yt inner join ( " +
                    "select uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "from work WHERE year LIKE :year " +
                    "group by day, month, year, taskuuid, useruuid " +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.day = ss.day and yt.created = ss.created and yt.taskuuid = ss.taskuuid and yt.useruuid = ss.useruuid;")
                    .addParameter("year", year)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00620:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByYearAndUserUUID(String year, String userUUID) {
        log.debug("WorkRepository.findByYearAndUserUUID");
        log.debug("year = [" + year + "], userUUID = [" + userUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "FROM work yt INNER JOIN( " +
                    "SELECT uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "FROM work WHERE useruuid LIKE :useruuid AND year = :year " +
                    "group by day, month, year, taskuuid, useruuid " +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.day = ss.day and yt.created = ss.created and yt.taskuuid = ss.taskuuid and yt.useruuid = ss.useruuid;")
                    .addParameter("useruuid", userUUID)
                    .addParameter("year", year)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00630:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByYearAndMonth(int year, int month) {
        log.debug("WorkRepository.findByYearAndMonth");
        log.debug("year = [" + year + "], month = [" + month + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("select yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "FROM work yt INNER JOIN( " +
                    "SELECT uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "FROM work WHERE month = :month AND year = :year " +
                    "group by day, month, year, taskuuid, useruuid " +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.day = ss.day and yt.created = ss.created and yt.taskuuid = ss.taskuuid and yt.useruuid = ss.useruuid;")
                    .addParameter("month", month)
                    .addParameter("year", year)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00640:", e);
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, Object>> findByYearAndMonthAndTaskUUIDAndUserUUID(String year, String month, String taskUUID, String userUUID) {
        log.debug("WorkRepository.findByYearAndMonthAndTaskUUIDAndUserUUID");
        log.debug("year = [" + year + "], month = [" + month + "], taskUUID = [" + taskUUID + "], userUUID = [" + userUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "FROM work yt INNER JOIN( " +
                    "SELECT uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "FROM work WHERE taskuuid LIKE :taskuuid AND useruuid LIKE useruuid AND month = :month AND year = :year " +
                    "GROUP BY day, month, year) ss " +
                    "ON yt.month = ss.month AND yt.year = ss.year AND yt.day = ss.day AND yt.created = ss.created AND yt.taskuuid = ss.taskuuid AND yt.useruuid = ss.useruuid;")
                    .addParameter("taskuuid", taskUUID)
                    .addParameter("useruuid", userUUID)
                    .addParameter("month", month)
                    .addParameter("year", year)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00650:", e);
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, Object>> findByYearAndMonthAndDay(int year, int month, int day) {
        log.debug("WorkRepository.findByYearAndMonthAndDayAndTaskUUIDAndUserUUID");
        log.debug("year = [" + year + "], month = [" + month + "], day = [" + day + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "FROM work yt INNER JOIN( " +
                    "SELECT uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "FROM work WHERE year = :year AND month = :month AND day = :day " +
                    "GROUP BY day, month, year) ss " +
                    "ON yt.month = ss.month AND yt.year = ss.year AND yt.day = ss.day AND yt.created = ss.created AND yt.taskuuid = ss.taskuuid AND yt.useruuid = ss.useruuid;")
                    .addParameter("month", month)
                    .addParameter("year", year)
                    .addParameter("day", day)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00660:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByYearAndMonthAndDayAndTaskUUIDAndUserUUID(int year, int month, int day, String taskUUID, String userUUID) {
        log.debug("WorkRepository.findByYearAndMonthAndDayAndTaskUUIDAndUserUUID");
        log.debug("year = [" + year + "], month = [" + month + "], day = [" + day + "], taskUUID = [" + taskUUID + "], userUUID = [" + userUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "FROM work yt INNER JOIN( " +
                    "SELECT uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "FROM work WHERE taskuuid LIKE :taskuuid AND useruuid LIKE :useruuid AND year = :year AND month = :month AND day = :day " +
                    "GROUP BY day, month, year) ss " +
                    "ON yt.month = ss.month AND yt.year = ss.year AND yt.day = ss.day AND yt.created = ss.created AND yt.taskuuid = ss.taskuuid AND yt.useruuid = ss.useruuid;")
                    .addParameter("taskuuid", taskUUID)
                    .addParameter("useruuid", userUUID)
                    .addParameter("month", month)
                    .addParameter("year", year)
                    .addParameter("day", day)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00660:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByTaskUUIDAndUserUUID(String taskUUID, String userUUID) {
        log.debug("WorkRepository.findByTaskUUIDAndUserUUID");
        log.debug("taskUUID = [" + taskUUID + "], userUUID = [" + userUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "FROM work yt INNER JOIN( " +
                    "SELECT uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "FROM work WHERE taskuuid LIKE :taskuuid AND useruuid LIKE :useruuid " +
                    "GROUP BY day, month, year) ss " +
                    "ON yt.created = ss.created AND yt.taskuuid = ss.taskuuid AND yt.useruuid = ss.useruuid;")
                    .addParameter("taskuuid", taskUUID)
                    .addParameter("useruuid", userUUID)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00670:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByYearAndMonthAndTaskUUID(int year, int month, String taskUUID) {
        log.debug("WorkRepository.findByYearAndMonthAndTaskUUID");
        log.debug("year = [" + year + "], month = [" + month + "], taskUUID = [" + taskUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "FROM work yt INNER JOIN( " +
                    "SELECT uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "FROM work WHERE taskuuid LIKE :taskuuid AND month = :month AND year = :year " +
                    "GROUP BY day, month, year) ss " +
                    "ON yt.month = ss.month AND yt.year = ss.year AND yt.day = ss.day AND yt.created = ss.created AND yt.taskuuid = ss.taskuuid AND yt.useruuid = ss.useruuid;")
                    .addParameter("taskuuid", taskUUID)
                    .addParameter("month", month)
                    .addParameter("year", year)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00680:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByYearAndTaskUUIDAndUserUUID(int year, String taskUUID, String userUUID) {
        log.debug("WorkRepository.findByYearAndTaskUUIDAndUserUUID");
        log.debug("year = [" + year + "], taskUUID = [" + taskUUID + "], userUUID = [" + userUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "FROM work yt INNER JOIN( " +
                    "SELECT uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "FROM work WHERE taskuuid LIKE :taskuuid AND useruuid LIKE :useruuid AND year = :year " +
                    "GROUP BY day, month, year) ss " +
                    "ON yt.month = ss.month AND yt.year = ss.year AND yt.day = ss.day AND yt.created = ss.created AND yt.taskuuid = ss.taskuuid AND yt.useruuid = ss.useruuid;")
                    .addParameter("taskuuid", taskUUID)
                    .addParameter("useruuid", userUUID)
                    .addParameter("year", year)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00690:", e);
        }
        return new ArrayList<>();
    }

    public double calculateTaskUserTotalDuration(String taskUUID, String userUUID) {
        log.debug("WorkRepository.calculateTaskUserTotalDuration");
        log.debug("taskUUID = [" + taskUUID + "], userUUID = [" + userUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return con.createQuery("SELECT sum(workduration) sum FROM ( " +
                    "SELECT yt.year, yt.month, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "FROM work yt INNER JOIN( " +
                    "SELECT uuid, day, month, year, workduration, taskuuid, useruuid, max(created) created " +
                    "FROM work WHERE taskuuid LIKE :taskuuid AND useruuid LIKE :useruuid " +
                    "GROUP BY day, month, year) ss " +
                    "ON yt.month = ss.month AND yt.year = ss.year AND yt.day = ss.day AND yt.created = ss.created " +
                    "AND yt.taskuuid = ss.taskuuid AND yt.useruuid = ss.useruuid " +
                    ") we;")
                    .addParameter("taskuuid", taskUUID)
                    .addParameter("useruuid", userUUID)
                    .executeScalar(Double.class);
        } catch (Exception e) {
            log.error("LOG00700:", e);
        }
        return 0.0;
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        log.debug("WorkRepository.create");
        log.debug("jsonNode = [" + jsonNode + "]");
        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("INSERT INTO work (uuid, day, month, year, taskuuid, useruuid, workduration, created)" +
                    " VALUES (:uuid, :day, :month, :year, :taskuuid, :useruuid, :workduration, :created)")
                    .addParameter("uuid", UUID.randomUUID().toString())
                    .addParameter("day", jsonNode.get("day").asInt())
                    .addParameter("month", jsonNode.get("month").asInt())
                    .addParameter("year", jsonNode.get("year").asInt())
                    .addParameter("taskuuid", jsonNode.get("taskuuid").asText())
                    .addParameter("useruuid", jsonNode.get("useruuid").asText())
                    .addParameter("workduration", jsonNode.get("workduration").asDouble())
                    .addParameter("created", Timestamp.from(Instant.now()))
                    .executeUpdate();

            //calculateTaskUserTotalDurationCache.invalidateAll();
        } catch (Exception e) {
            log.error("LOG00710:", e);
        }
    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {

    }
}
