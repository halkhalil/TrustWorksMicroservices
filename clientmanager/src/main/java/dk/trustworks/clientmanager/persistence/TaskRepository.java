package dk.trustworks.clientmanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.persistence.GenericRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hans on 17/03/15.
 */
public class TaskRepository extends GenericRepository {

    private static final Logger logger = LogManager.getLogger();

    public TaskRepository() {
        super();
    }

    public List<Map<String, Object>> findByProjectUUID(String projectUUID) {
        logger.debug("TaskRepository.findByProjectUUID");
        logger.debug("projectUUID = [" + projectUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM task WHERE projectuuid LIKE :projectuuid")
                    .addParameter("projectuuid", projectUUID)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            logger.error("LOG00430:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByProjectUUIDOrderByNameAsc(String projectUUID) {
        logger.debug("TaskRepository.findByProjectUUIDOrderByNameAsc");
        logger.debug("projectUUID = [" + projectUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM task WHERE projectuuid LIKE :projectuuid ORDER BY name ASC")
                    .addParameter("projectuuid", projectUUID)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            logger.error("LOG00440:", e);
        }
        return new ArrayList<>();
    }

    public void create(JsonNode jsonNode) throws SQLException {
        logger.debug("TaskRepository.create");
        logger.debug("jsonNode = [" + jsonNode + "]");
        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("INSERT INTO task (uuid, type, na" +
                    "me, projectuuid) VALUES (:uuid, :type, :name, :projectuuid)")
                    .addParameter("uuid", jsonNode.get("uuid").asText(UUID.randomUUID().toString()))
                    .addParameter("type", jsonNode.get("type").asText("KONSULENT"))
                    .addParameter("name", jsonNode.get("name").asText(""))
                    .addParameter("projectuuid", jsonNode.get("projectuuid").asText())
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00450:", e);
        }
    }

    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        logger.debug("TaskRepository.update");
        logger.debug("jsonNode = [" + jsonNode + "], uuid = [" + uuid + "]");
        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("UPDATE task t SET t.type = :type, t.name = :name WHERE t.uuid LIKE :uuid")
                    .addParameter("uuid", jsonNode.get("uuid").asText())
                    .addParameter("type", jsonNode.get("type").asText())
                    .addParameter("name", jsonNode.get("name").asText())
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00460:", e);
        }
    }
}