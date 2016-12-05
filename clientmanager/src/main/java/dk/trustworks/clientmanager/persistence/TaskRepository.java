package dk.trustworks.clientmanager.persistence;

import dk.trustworks.framework.model.Project;
import dk.trustworks.framework.model.Task;
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
public class TaskRepository {

    private static final Logger logger = LogManager.getLogger();
    private final Sql2o sql2o;

    public TaskRepository(DataSource ds) {
        sql2o = new Sql2o(ds);
    }

    public List<Task> findAll() {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM task")
                    .executeAndFetch(Task.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Task> findAllByProjectUUIDs(List<Project> projects) {
        StringBuilder builder = new StringBuilder();
        for( int i = 0 ; i < projects.size(); i++ ) {
            builder.append("'"+projects.get(i).uuid+"',");
        }

        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM task WHERE projectuuid IN ("+builder.deleteCharAt( builder.length() -1 ).toString()+") ORDER BY name ASC")
                    .executeAndFetch(Task.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Task findByUUID(String uuid) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM task WHERE uuid LIKE :uuid")
                    .addParameter("uuid", uuid)
                    .executeAndFetchFirst(Task.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Task();
    }

    public List<Task> findByProjectUUID(String projectUUID) {
        logger.debug("TaskRepository.findByProjectUUID");
        logger.debug("projectUUID = [" + projectUUID + "]");
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM task WHERE projectuuid LIKE :projectuuid ORDER BY name")
                    .addParameter("projectuuid", projectUUID)
                    .executeAndFetch(Task.class);
        } catch (Exception e) {
            logger.error("LOG00430:", e);
        }
        return new ArrayList<>();
    }

    public void create(Task task) throws SQLException {
        logger.debug("TaskRepository.create");
        task.uuid = UUID.randomUUID().toString();
        task.type = "KONSULENT";

        try (Connection con = sql2o.open()) {
            con.createQuery("INSERT INTO task (uuid, type, name, projectuuid) " +
                    "VALUES (:uuid, :type, :name, :projectuuid)")
                    .bind(task)
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00450:", e);
        }
    }

    public void update(Task task, String uuid) throws SQLException {
        logger.debug("TaskRepository.update");
        task.uuid = uuid;

        try (Connection con = sql2o.open()) {
            con.createQuery("UPDATE task t SET t.type = :type, t.name = :name WHERE t.uuid LIKE :uuid")
                    .bind(task)
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00460:", e);
        }
    }
}