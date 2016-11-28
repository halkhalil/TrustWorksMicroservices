package dk.trustworks.clientmanager.persistence;

import dk.trustworks.clientmanager.model.Project;
import dk.trustworks.clientmanager.model.Task;
import dk.trustworks.clientmanager.model.TaskWorkerConstraintBudget;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.jooby.Err;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by hans on 17/03/15.
 */
public class TaskWorkerConstraintBudgetRepository {

    private static final Logger log = LogManager.getLogger();
    private final Sql2o sql2o;

    public TaskWorkerConstraintBudgetRepository(DataSource ds) {
        sql2o = new Sql2o(ds);
    }

    public List<TaskWorkerConstraintBudget> findAll() {
        try (Connection con = sql2o.open()) {
            List<TaskWorkerConstraintBudget> taskWorkerConstraintBudgets = con.createQuery("select * from taskworkerconstraintbudget")
                    .executeAndFetch(TaskWorkerConstraintBudget.class);
            con.close();
            return taskWorkerConstraintBudgets;
        } catch (Exception e) {
            log.error("LOG00474:", e);
        }
        return new ArrayList<>();
    }

    public List<TaskWorkerConstraintBudget> findByTaskUUIDsWithHistory(List<Task> tasks) {
        StringBuilder builder = new StringBuilder();
        for( int i = 0 ; i < tasks.size(); i++ ) {
            builder.append("'"+tasks.get(i).uuid+"',");
        }

        try (Connection con = sql2o.open()) {
            List<TaskWorkerConstraintBudget> taskWorkerConstraintBudgets = con.createQuery("SELECT * FROM taskworkerconstraintbudget " +
                    "WHERE taskuuid IN ("+builder.deleteCharAt( builder.length() -1 ).toString()+") " +
                    "ORDER BY year ASC, month ASC, created DESC;")
                    .executeAndFetch(TaskWorkerConstraintBudget.class);
            con.close();
            return taskWorkerConstraintBudgets;
        } catch (Exception e) {
            log.error("LOG00474:", e);
        }
        return new ArrayList<>();
    }

    public List<TaskWorkerConstraintBudget> findByTaskUUIDAndUserUUID(String taskUUID, String userUUID) {
        System.out.println("TaskWorkerConstraintBudgetRepository.findByTaskUUIDAndUserUUID");
        System.out.println("taskUUID = [" + taskUUID + "], userUUID = [" + userUUID + "]");
        try (Connection con = sql2o.open()) {
            List<TaskWorkerConstraintBudget> taskWorkerConstraintBudget = con.createQuery("" +
                    "select yt.month, yt.year, yt.created, yt.budget, yt.taskuuid, yt.useruuid " +
                    "from taskworkerconstraintbudget yt " +
                    "inner join( " +
                    "select uuid, month, year, taskuuid, useruuid, max(created) created " +
                    "from taskworkerconstraintbudget WHERE taskuuid LIKE :taskuuid AND useruuid LIKE :useruuid " +
                    "group by month, year " +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.created = ss.created and yt.taskuuid = ss.taskuuid and yt.useruuid = ss.useruuid;")
                    .addParameter("taskuuid", taskUUID)
                    .addParameter("useruuid", userUUID)
                    .executeAndFetch(TaskWorkerConstraintBudget.class);
            System.out.println("taskWorkerConstraintBudget.size() = " + taskWorkerConstraintBudget.size());
            con.close();
            return taskWorkerConstraintBudget;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<String> getUniqueUsersBudgetsPerTask(String taskUUID) {
        System.out.println("TaskWorkerConstraintBudgetRepository.getUniqueUsersBudgetsPerTask");
        System.out.println("taskUUID = [" + taskUUID + "]");
        try (Connection con = sql2o.open()) {
            List<String> userUUIDs = con.createQuery("" +
                    "SELECT DISTINCT useruuid FROM taskworkerconstraintbudget " +
                    "WHERE taskuuid LIKE :taskuuid;")
                    .addParameter("taskuuid", taskUUID)
                    .executeAndFetch(String.class);
            con.close();
            return userUUIDs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Deprecated
    public List<TaskWorkerConstraintBudget> findByTaskUUID(String taskUUID) {
        log.debug("TaskWorkerConstraintBudgetRepository.findByTaskWorkerConstraintUUID");
        System.out.println("taskUUID = [" + taskUUID + "]");
        try (Connection con = sql2o.open()) {
            List<TaskWorkerConstraintBudget> taskWorkerConstraintBudget = con.createQuery("" +
                    "select yt.month, yt.year, yt.created, yt.budget, yt.taskuuid, yt.useruuid " +
                    "from taskworkerconstraintbudget yt " +
                    "inner join( " +
                    "select uuid, month, year, taskuuid, useruuid, max(created) created " +
                    "from taskworkerconstraintbudget WHERE taskuuid LIKE :taskuuid " +
                    "group by month, year " +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.created = ss.created and yt.taskuuid = ss.taskuuid and yt.useruuid = ss.useruuid;")
                    .addParameter("taskuuid", taskUUID)
                    .executeAndFetch(TaskWorkerConstraintBudget.class);
            System.out.println("taskWorkerConstraintBudget.size() = " + taskWorkerConstraintBudget.size());
            con.close();
            throw new RuntimeException("NOT ALLOWED!!");
            //return taskWorkerConstraintBudget;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("NOT ALLOWED!!");
        //return new ArrayList<>();
    }
/*
    public List<TaskWorkerConstraintBudget> findByTaskWorkerConstraintUUID(String taskWorkerConstraintUUID) {
        log.debug("TaskWorkerConstraintBudgetRepository.findByTaskWorkerConstraintUUID");
        log.debug("taskWorkerConstraintUUID = " + taskWorkerConstraintUUID);
        try (Connection con = sql2o.open()) {
            return con.createQuery("" +
                    "select yt.month, yt.year, yt.created, yt.budget, yt.taskworkerconstraintuuid " +
                    "from taskworkerconstraintbudget yt " +
                    "inner join( " +
                    "select uuid, month, year, taskworkerconstraintuuid, max(created) created " +
                    "from taskworkerconstraintbudget WHERE taskworkerconstraintuuid LIKE :taskworkerconstraintuuid " +
                    "group by month, year " +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.created = ss.created and yt.taskworkerconstraintuuid = ss.taskworkerconstraintuuid;")
                    .addParameter("taskworkerconstraintuuid", taskWorkerConstraintUUID)
                    .executeAndFetch(TaskWorkerConstraintBudget.class);
        } catch (Exception e) {
            log.error("LOG00470:", e);
        }
        return new ArrayList<>();
    }

    public List<TaskWorkerConstraintBudget> findByMonthAndYear(int month, int year) {
        log.debug("TaskWorkerConstraintBudgetRepository.findByMonthAndYear");
        log.debug("month = [" + month + "], year = [" + year + "]");
        try (Connection con = sql2o.open()) {
            return con.createQuery("" +
                    "select yt.month, yt.year, yt.created, yt.budget, yt.taskworkerconstraintuuid " +
                    "from taskworkerconstraintbudget yt " +
                    "inner join( " +
                    "select uuid, month, year, taskworkerconstraintuuid, max(created) created " +
                    "from taskworkerconstraintbudget WHERE month = :month and year = :year " +
                    "group by month, year, taskworkerconstraintuuid " +
                    ") ss on yt.created = ss.created and yt.taskworkerconstraintuuid = ss.taskworkerconstraintuuid;")
                    .addParameter("month", month)
                    .addParameter("year", year)
                    .executeAndFetch(TaskWorkerConstraintBudget.class);
        } catch (Exception e) {
            log.error("LOG00480:", e);
        }
        return new ArrayList<>();
    }

    public List<TaskWorkerConstraintBudget> findByYear(int year) {
        log.debug("TaskWorkerConstraintBudgetRepository.findByYear");
        log.debug("year = [" + year + "]");
        try (Connection con = sql2o.open()) {
            return con.createQuery("" +
                    "select yt.month, yt.year, yt.useruuid, yt.taskuuid, yt.created, yt.budget " +
                    "from taskworkerconstraintbudget yt " +
                    "inner join( " +
                    "select uuid, month, year, useruuid, taskuuid, max(created) created " +
                    "from taskworkerconstraintbudget WHERE year = :year " +
                    "group by month, year, useruuid, taskuuid " +
                    ") ss on yt.created = ss.created and yt.useruuid = ss.useruuid and yt.taskuuid = ss.taskuuid;")
                    .addParameter("year", year)
                    .executeAndFetch(TaskWorkerConstraintBudget.class);
        } catch (Exception e) {
            log.error("LOG00480:", e);
        }
        return new ArrayList<>();
    }
*/
    public List<TaskWorkerConstraintBudget> findByYearAndUser(int year, String userUUID) {
        log.debug("TaskWorkerConstraintBudgetRepository.findByYear");
        log.debug("year = [" + year + "]");
        log.debug("userUUID = [" + userUUID + "]");
        try (Connection con = sql2o.open()) {
            return con.createQuery("" +
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
                    .executeAndFetch(TaskWorkerConstraintBudget.class);
        } catch (Exception e) {
            log.error("LOG00480:", e);
        }
        return new ArrayList<>();
    }
/*
    public List<TaskWorkerConstraintBudget> findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate(String taskWorkerConstraintUUID, int month, int year, LocalDate date) {
        log.debug("TaskWorkerConstraintBudgetRepository.findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate");
        try (Connection con = sql2o.open()) {
            return con.createQuery("" +
                    "select yt.month, yt.year, yt.created, yt.budget, yt.taskworkerconstraintuuid " +
                    "from taskworkerconstraintbudget yt " +
                    "inner join( " +
                    "select uuid, month, year, taskworkerconstraintuuid, max(created) created " +
                    "from taskworkerconstraintbudget WHERE taskworkerconstraintuuid LIKE :taskworkerconstraintuuid AND created < :created AND month = :month AND year = :year " +
                    "group by month, year " +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.created = ss.created and yt.taskworkerconstraintuuid = ss.taskworkerconstraintuuid;")
                    .addParameter("taskworkerconstraintuuid", taskWorkerConstraintUUID)
                    .addParameter("created", date.toDateTimeAtCurrentTime())
                    .addParameter("month", month)
                    .addParameter("year", year)
                    .executeAndFetch(TaskWorkerConstraintBudget.class);
        } catch (Exception e) {
            log.error("LOG00490:", e);
        }
        return new ArrayList<>();
    }
*/
    public List<TaskWorkerConstraintBudget> findByMonthAndYearAndDate(int month, int year, LocalDate date) {
        log.debug("TaskWorkerConstraintBudgetRepository.findByTaskWorkerConstraintUUIDAndMonthAndYearAndDate");
        try (Connection con = sql2o.open()) {
            return con.createQuery("" +
                    "select yt.month, yt.year, yt.useruuid, yt.taskuuid, yt.created, yt.budget " +
                    "from taskworkerconstraintbudget yt " +
                    "inner join( " +
                    "select uuid, month, year, useruuid, taskuuid, max(created) created " +
                    "from taskworkerconstraintbudget WHERE created < :created AND month = :month AND year = :year " +
                    "group by month, year, useruuid, taskuuid" +
                    ") ss on yt.month = ss.month and yt.year = ss.year and yt.created = ss.created and yt.useruuid = ss.useruuid and yt.taskuuid = ss.taskuuid;")
                    .addParameter("created", date.toDateTimeAtCurrentTime())
                    .addParameter("month", month)
                    .addParameter("year", year)
                    .executeAndFetch(TaskWorkerConstraintBudget.class);
        } catch (Exception e) {
            log.error("LOG00490:", e);
        }
        return new ArrayList<>();
    }
/*
    public double calculateTotalTaskBudget(String taskUUID) {
        log.debug("TaskWorkerConstraintBudgetRepository.calculateTotalTaskBudget");
        log.debug("taskUUID = [" + taskUUID + "]");
        try (Connection con = sql2o.open()) {
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
*/
    public void create(TaskWorkerConstraintBudget taskWorkerConstraintBudget) throws SQLException {
        log.debug("TaskWorkerConstraintBudgetRepository.create");
        taskWorkerConstraintBudget.uuid = UUID.randomUUID().toString();
        taskWorkerConstraintBudget.created = DateTime.now();
        taskWorkerConstraintBudget.taskworkerconstraintuuid = UUID.randomUUID().toString();
        try (Connection con = sql2o.open()) {
            con.createQuery("INSERT INTO taskworkerconstraintbudget (uuid, budget, month, year, taskworkerconstraintuuid, useruuid, taskuuid, created) " +
                    "VALUES (:uuid, :budget, :month, :year, :taskworkerconstraintuuid, :useruuid, :taskuuid, :created)")
                    .bind(taskWorkerConstraintBudget)
                    .executeUpdate();
            con.close();
        } catch (Exception e) {
            log.error("LOG00510:", e);
        }
        log.exit();
    }

    public void update(TaskWorkerConstraintBudget taskWorkerConstraintBudget, String uuid) throws SQLException {
        create(taskWorkerConstraintBudget);
    }

    public void addUserTask(TaskWorkerConstraintBudget taskWorkerConstraintBudget) {
        Connection con = sql2o.open();
        con.createQuery("UPDATE taskworkerconstraintbudget p SET p.useruuid = :useruuid, p.taskuuid = :taskuuid WHERE p.uuid LIKE :uuid")
                .bind(taskWorkerConstraintBudget)
                .executeUpdate();
        con.close();
    }
}
