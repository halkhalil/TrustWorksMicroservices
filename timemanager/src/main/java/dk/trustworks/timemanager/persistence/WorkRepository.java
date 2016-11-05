package dk.trustworks.timemanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.timemanager.dto.Work;
import org.apache.tomcat.jni.Local;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by hans on 17/03/15.
 */
public class WorkRepository {

    private final Sql2o sql2o;

    public WorkRepository(DataSource ds) {
        sql2o = new Sql2o(ds);
    }

    public List<Work> findByTaskUUID(String taskUUID) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("select yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "from work yt inner join( " +
                    "select uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "from work WHERE taskuuid LIKE :taskuuid " +
                    "group by day, month, year " +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.day = ss.day and yt.created = ss.created and yt.taskuuid = ss.taskuuid and yt.useruuid = ss.useruuid;")
                    .addParameter("taskuuid", taskUUID)
                    .executeAndFetch(Work.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Work> findByPeriod(LocalDate periodStart, LocalDate periodEnd) {
        int startYear = periodStart.getYear();
        int startMonth = periodStart.getMonthOfYear();
        int startDay = periodStart.getDayOfMonth();
        int endYear = periodEnd.getYear();
        int endMonth = periodEnd.getMonthOfYear();
        int endDay = periodEnd.getDayOfMonth();
        return new ArrayList<>();
    }

    public List<Work> findByYear(int year) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("select yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "from work yt inner join ( " +
                    "select uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "from work WHERE year LIKE :year " +
                    "group by day, month, year, taskuuid, useruuid " +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.day = ss.day and yt.created = ss.created and yt.taskuuid = ss.taskuuid and yt.useruuid = ss.useruuid;")
                    .addParameter("year", year)
                    .executeAndFetch(Work.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Work> findByYearAndUserUUID(int year, String userUUID) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "FROM work yt INNER JOIN( " +
                    "SELECT uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "FROM work WHERE useruuid LIKE :useruuid AND year = :year " +
                    "group by day, month, year, taskuuid, useruuid " +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.day = ss.day and yt.created = ss.created and yt.taskuuid = ss.taskuuid and yt.useruuid = ss.useruuid;")
                    .addParameter("useruuid", userUUID)
                    .addParameter("year", year)
                    .executeAndFetch(Work.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Work> findByYearAndMonth(int year, int month) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("select yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "FROM work yt INNER JOIN( " +
                    "SELECT uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "FROM work WHERE month = :month AND year = :year " +
                    "group by day, month, year, taskuuid, useruuid " +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.day = ss.day and yt.created = ss.created and yt.taskuuid = ss.taskuuid and yt.useruuid = ss.useruuid;")
                    .addParameter("month", month)
                    .addParameter("year", year)
                    .executeAndFetch(Work.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Work> findByYearAndMonthAndTaskUUIDAndUserUUID(int year, int month, String taskUUID, String userUUID) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "FROM work yt INNER JOIN( " +
                    "SELECT uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "FROM work WHERE taskuuid LIKE :taskuuid AND useruuid LIKE useruuid AND month = :month AND year = :year " +
                    "GROUP BY day, month, year) ss " +
                    "ON yt.month = ss.month AND yt.year = ss.year AND yt.day = ss.day AND yt.created = ss.created AND yt.taskuuid = ss.taskuuid AND yt.useruuid = ss.useruuid;")
                    .addParameter("taskuuid", taskUUID)
                    .addParameter("useruuid", userUUID)
                    .addParameter("month", month)
                    .addParameter("year", year)
                    .executeAndFetch(Work.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Work> findByYearAndMonthAndDay(int year, int month, int day) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("select yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "FROM work yt INNER JOIN( " +
                    "SELECT uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "FROM work WHERE day = :day AND month = :month AND year = :year " +
                    "group by day, month, year, taskuuid, useruuid " +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.day = ss.day and yt.created = ss.created and yt.taskuuid = ss.taskuuid and yt.useruuid = ss.useruuid;")
                    .addParameter("day", day)
                    .addParameter("month", month)
                    .addParameter("year", year)
                    .executeAndFetch(Work.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Work> findByYearAndMonthAndDayAndTaskUUIDAndUserUUID(int year, int month, int day, String taskUUID, String userUUID) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
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
                    .executeAndFetch(Work.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Work> findByTaskUUIDAndUserUUID(String taskUUID, String userUUID) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "FROM work yt INNER JOIN( " +
                    "SELECT uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "FROM work WHERE taskuuid LIKE :taskuuid AND useruuid LIKE :useruuid " +
                    "GROUP BY day, month, year) ss " +
                    "ON yt.created = ss.created AND yt.taskuuid = ss.taskuuid AND yt.useruuid = ss.useruuid;")
                    .addParameter("taskuuid", taskUUID)
                    .addParameter("useruuid", userUUID)
                    .executeAndFetch(Work.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Work> findByYearAndMonthAndTaskUUID(int year, int month, String taskUUID) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "FROM work yt INNER JOIN( " +
                    "SELECT uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "FROM work WHERE taskuuid LIKE :taskuuid AND month = :month AND year = :year " +
                    "GROUP BY day, month, year) ss " +
                    "ON yt.month = ss.month AND yt.year = ss.year AND yt.day = ss.day AND yt.created = ss.created AND yt.taskuuid = ss.taskuuid AND yt.useruuid = ss.useruuid;")
                    .addParameter("taskuuid", taskUUID)
                    .addParameter("month", month)
                    .addParameter("year", year)
                    .executeAndFetch(Work.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Work> findByYearAndTaskUUIDAndUserUUID(int year, String taskUUID, String userUUID) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT yt.month, yt.year, yt.day, yt.created, yt.workduration, yt.taskuuid, yt.useruuid " +
                    "FROM work yt INNER JOIN( " +
                    "SELECT uuid, month, year, day, workduration, taskuuid, useruuid, max(created) created " +
                    "FROM work WHERE taskuuid LIKE :taskuuid AND useruuid LIKE :useruuid AND year = :year " +
                    "GROUP BY day, month, year) ss " +
                    "ON yt.month = ss.month AND yt.year = ss.year AND yt.day = ss.day AND yt.created = ss.created AND yt.taskuuid = ss.taskuuid AND yt.useruuid = ss.useruuid;")
                    .addParameter("taskuuid", taskUUID)
                    .addParameter("useruuid", userUUID)
                    .addParameter("year", year)
                    .executeAndFetch(Work.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public double calculateTaskUserTotalDuration(String taskUUID, String userUUID) {
        try (Connection con = sql2o.open()) {
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
            throw new RuntimeException(e);
        }
    }

    public void create(Work work) throws SQLException {
        work.uuid = UUID.randomUUID().toString();
        work.created = DateTime.now();
        try (Connection con = sql2o.open()) {
            con.createQuery("INSERT INTO work (uuid, day, month, year, taskuuid, useruuid, workduration, created)" +
                    " VALUES (:uuid, :day, :month, :year, :taskuuid, :useruuid, :workduration, :created)").bind(work)
                    /*
                    .addParameter("uuid", UUID.randomUUID().toString())
                    .addParameter("day", jsonNode.get("day").asInt())
                    .addParameter("month", jsonNode.get("month").asInt())
                    .addParameter("year", jsonNode.get("year").asInt())
                    .addParameter("taskuuid", jsonNode.get("taskuuid").asText())
                    .addParameter("useruuid", jsonNode.get("useruuid").asText())
                    .addParameter("workduration", jsonNode.get("workduration").asDouble())
                    .addParameter("created", Timestamp.from(Instant.now()))
                    */
                    .executeUpdate();

            //calculateTaskUserTotalDurationCache.invalidateAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Work work, String uuid) throws SQLException {

    }
}
