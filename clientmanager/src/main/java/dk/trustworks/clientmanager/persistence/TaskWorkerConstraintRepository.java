package dk.trustworks.clientmanager.persistence;

import dk.trustworks.framework.model.Task;
import dk.trustworks.framework.model.TaskWorkerConstraint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class TaskWorkerConstraintRepository {

    private static final Logger logger = LogManager.getLogger();
    private final Sql2o sql2o;

    public TaskWorkerConstraintRepository(DataSource ds) {
        sql2o = new Sql2o(ds);
    }

    public List<TaskWorkerConstraint> findAll() {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM taskworkerconstraint").executeAndFetch(TaskWorkerConstraint.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<TaskWorkerConstraint> findAllByTaskUUIDs(List<Task> tasks) {
        StringBuilder builder = new StringBuilder();
        for( int i = 0 ; i < tasks.size(); i++ ) {
            builder.append("'"+tasks.get(i).uuid+"',");
        }

        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM taskworkerconstraint WHERE taskuuid IN ("+builder.deleteCharAt( builder.length() -1 ).toString()+")")
                    .executeAndFetch(TaskWorkerConstraint.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public TaskWorkerConstraint findByUUID(String uuid) {
        try (Connection con = sql2o.open()) {
            TaskWorkerConstraint taskWorkerConstraint = con.createQuery("SELECT * FROM taskworkerconstraint WHERE uuid LIKE :uuid")
                    .addParameter("uuid", uuid)
                    .executeAndFetchFirst(TaskWorkerConstraint.class);
            con.close();
            return taskWorkerConstraint;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new TaskWorkerConstraint();
    }

    public List<TaskWorkerConstraint> findByTaskUUID(String taskUUID) {
        logger.debug("TaskWorkerConstraintRepository.findByTaskUUID");
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM taskworkerconstraint WHERE taskuuid LIKE :taskuuid")
                    .addParameter("taskuuid", taskUUID)
                    .executeAndFetch(TaskWorkerConstraint.class);
        } catch (Exception e) {
            logger.error("LOG00520:", e);
        }
        return new ArrayList<>();
    }

    public TaskWorkerConstraint findByTaskUUIDAndUserUUID(String taskUUID, String userUUID) {
        logger.debug("TaskWorkerConstraintRepository.findByTaskUUIDAndUserUUID");
        logger.debug("taskUUID = [" + taskUUID + "], userUUID = [" + userUUID + "]");
        try (Connection con = sql2o.open()) {
            List<TaskWorkerConstraint> list = con.createQuery("SELECT * FROM taskworkerconstraint WHERE taskuuid LIKE :taskuuid AND useruuid LIKE :useruuid")
                    .addParameter("taskuuid", taskUUID)
                    .addParameter("useruuid", userUUID)
                    .executeAndFetch(TaskWorkerConstraint.class);

            if (list.size() > 0) return list.get(0);

            TaskWorkerConstraint result = new TaskWorkerConstraint("", 0.0, userUUID, taskUUID);
            return result;
        } catch (Exception e) {
            logger.error("LOG00530:", e);
        }
        TaskWorkerConstraint result = new TaskWorkerConstraint("", 0.0, userUUID, taskUUID);
        return result;
    }

    public void create(TaskWorkerConstraint taskWorkerConstraint) throws SQLException {
        logger.debug("TaskWorkerConstraintRepository.create");
        taskWorkerConstraint.uuid = UUID.randomUUID().toString();
        //taskWorkerConstraint.price = 0.0;

        try (Connection con = sql2o.open()) {
            con.createQuery("INSERT INTO taskworkerconstraint (uuid, price, taskuuid, useruuid) VALUES (:uuid, :price, :taskuuid, :useruuid)")
                    .bind(taskWorkerConstraint)
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00540:", e);
        }
    }

    public void update(TaskWorkerConstraint taskWorkerConstraint, String uuid) throws SQLException {
        logger.debug("TaskWorkerConstraintRepository.update");
        taskWorkerConstraint.uuid = uuid;

        try (Connection con = sql2o.open()) {
             con.createQuery("UPDATE taskworkerconstraint SET price = :price WHERE uuid LIKE :uuid")
                    .bind(taskWorkerConstraint)
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00550:", e);
        }
    }
}
