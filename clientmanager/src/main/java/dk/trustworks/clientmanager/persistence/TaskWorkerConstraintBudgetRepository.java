package dk.trustworks.clientmanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.persistence.GenericRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

/**
 * Created by hans on 17/03/15.
 */
public class TaskWorkerConstraintBudgetRepository extends GenericRepository {

    private static final Logger log = LogManager.getLogger();

    public TaskWorkerConstraintBudgetRepository() {
        super();
    }

    public List<Map<String, Object>> findByTaskWorkerConstraintUUID(String taskWorkerConstraintUUID) {
        log.debug("TaskWorkerConstraintBudgetRepository.findByTaskWorkerConstraintUUID");
        log.debug("taskWorkerConstraintUUID = " + taskWorkerConstraintUUID);
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("" +
                    "select yt.month, yt.year, yt.created, yt.budget, yt.taskworkerconstraintuuid " +
                    "from taskworkerconstraintbudget yt " +
                    "inner join( " +
                    "select uuid, month, year, taskworkerconstraintuuid, max(created) created " +
                    "from taskworkerconstraintbudget WHERE taskworkerconstraintuuid LIKE :taskworkerconstraintuuid " +
                    "group by month, year " +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.created = ss.created and yt.taskworkerconstraintuuid = ss.taskworkerconstraintuuid;")
                    .addParameter("taskworkerconstraintuuid", taskWorkerConstraintUUID)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00470:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByMonthAndYear(int month, int year) {
        log.debug("TaskWorkerConstraintBudgetRepository.findByMonthAndYear");
        log.debug("month = [" + month + "], year = [" + year + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("" +
                    "select yt.month, yt.year, yt.created, yt.budget, yt.taskworkerconstraintuuid " +
                    "from taskworkerconstraintbudget yt " +
                    "inner join( " +
                    "select uuid, month, year, taskworkerconstraintuuid, max(created) created " +
                    "from taskworkerconstraintbudget WHERE month = :month and year = :year " +
                    "group by month, year, taskworkerconstraintuuid " +
                    ") ss on yt.created = ss.created and yt.taskworkerconstraintuuid = ss.taskworkerconstraintuuid;")
                    .addParameter("month", month)
                    .addParameter("year", year)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00480:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByYear(int year) {
        log.debug("TaskWorkerConstraintBudgetRepository.findByYear");
        log.debug("year = [" + year + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("" +
                    "select yt.month, yt.year, yt.created, yt.budget, yt.taskworkerconstraintuuid " +
                    "from taskworkerconstraintbudget yt " +
                    "inner join( " +
                    "select uuid, month, year, taskworkerconstraintuuid, max(created) created " +
                    "from taskworkerconstraintbudget WHERE year = :year " +
                    "group by month, year, taskworkerconstraintuuid " +
                    ") ss on yt.created = ss.created and yt.taskworkerconstraintuuid = ss.taskworkerconstraintuuid;")
                    .addParameter("year", year)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00480:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByYearAndUser(int year, String userUUID) {
        log.debug("TaskWorkerConstraintBudgetRepository.findByYear");
        log.debug("year = [" + year + "]");
        log.debug("userUUID = [" + userUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("" +
                    "select yt.month, yt.year, yt.created, yt.budget, yt.taskworkerconstraintuuid  " +
                    "from taskworkerconstraintbudget yt " +
                    "inner join( " +
                    "select uuid, month, year, taskworkerconstraintuuid, max(created) created " +
                    "from taskworkerconstraintbudget WHERE year = :year " +
                    "group by month, year, taskworkerconstraintuuid " +
                    ") ss on yt.created = ss.created and yt.taskworkerconstraintuuid = ss.taskworkerconstraintuuid " +
                    "INNER JOIN (select * from taskworkerconstraint twc where twc.useruuid LIKE :useruuid) r ON r.uuid = ss.taskworkerconstraintuuid;")
                    .addParameter("year", year)
                    .addParameter("useruuid", userUUID)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00480:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(String taskWorkerConstraintUUID, int month, int year, Date datetime) {
        log.debug("TaskWorkerConstraintBudgetRepository.findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate");
        log.debug("taskWorkerConstraintUUID = [" + taskWorkerConstraintUUID + "], month = [" + month + "], year = [" + year + "], ldt = [" + datetime + "]");
        if (taskWorkerConstraintUUID.equals("6af071fa-6a95-44e5-8634-9820e0887500") && month == 7) {
            System.out.println("new SimpleDateFormat(\"yyyy-MM-dd HH:mm\").format(datetime) = " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(datetime));
        }
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("" +
                    "select yt.month, yt.year, yt.created, yt.budget, yt.taskworkerconstraintuuid " +
                    "from taskworkerconstraintbudget yt " +
                    "inner join( " +
                    "select uuid, month, year, taskworkerconstraintuuid, max(created) created " +
                    "from taskworkerconstraintbudget WHERE taskworkerconstraintuuid LIKE :taskworkerconstraintuuid AND created < :created AND month = :month AND year = :year " +
                    "group by month, year " +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.created = ss.created and yt.taskworkerconstraintuuid = ss.taskworkerconstraintuuid;")
                    .addParameter("taskworkerconstraintuuid", taskWorkerConstraintUUID)
                    .addParameter("created", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(datetime))
                    .addParameter("month", month)
                    .addParameter("year", year)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00490:", e);
        }
        return new ArrayList<>();
    }

    public double calculateTotalTaskBudget(String taskUUID) {
        log.debug("TaskWorkerConstraintBudgetRepository.calculateTotalTaskBudget");
        log.debug("taskUUID = [" + taskUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return con.createQuery("" +
                    "SELECT sum(budget) sum FROM ( " +
                    "select yt.month, yt.year, yt.created, yt.budget, yt.taskworkerconstraintuuid " +
                    "from taskworkerconstraintbudget yt " +
                    "inner join( " +
                    "select uuid, month, year, taskworkerconstraintuuid, max(created) created " +
                    "from taskworkerconstraintbudget WHERE taskworkerconstraintuuid LIKE :taskworkerconstraintuuid " +
                    "group by month, year " +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.created = ss.created and yt.taskworkerconstraintuuid = ss.taskworkerconstraintuuid" +
                    ") we;")
                    .addParameter("taskworkerconstraintuuid", taskUUID)
                    .executeScalar(Double.class);
        } catch (Exception e) {
            log.error("LOG00500:", e);
        }
        return 0.0;
    }

    public void create(JsonNode jsonNode) throws SQLException {
        log.entry(jsonNode);
        log.debug("TaskWorkerConstraintBudgetRepository.create");
        log.debug("jsonNode = [" + jsonNode + "]");
        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("INSERT INTO taskworkerconstraintbudget (uuid, budget, month, year, taskworkerconstraintuuid, created) " +
                    "VALUES (:uuid, :budget, :month, :year, :taskworkerconstraintuuid, :created)")
                    .addParameter("uuid", jsonNode.get("uuid").asText(UUID.randomUUID().toString()))
                    .addParameter("budget", jsonNode.get("budget").asDouble(0.0))
                    .addParameter("month", jsonNode.get("month").asInt(0))
                    .addParameter("year", jsonNode.get("year").asInt(0))
                    .addParameter("taskworkerconstraintuuid", jsonNode.get("taskworkerconstraintuuid").asText())
                    .addParameter("created", Timestamp.from(Instant.now()))
                    .executeUpdate();
        } catch (Exception e) {
            log.error("LOG00510:", e);
        }
        log.exit();

    }

    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        log.entry(jsonNode, uuid);
        log.debug("Update taskworkerconstraintbudget: " + jsonNode);
        log.error("LOG00250: NOT ALLOWED");
        throw new RuntimeException("Not allowed");
    }
}
