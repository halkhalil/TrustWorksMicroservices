package dk.trustworks.clientmanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.persistence.GenericRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by hans on 17/03/15.
 */
public class TaskWorkerConstraintRepository extends GenericRepository {

    private static final Logger logger = LogManager.getLogger();

    public TaskWorkerConstraintRepository() {
        super();
    }

    public List<Map<String, Object>> findByTaskUUID(String taskUUID) {
        logger.debug("TaskWorkerConstraintRepository.findByTaskUUID");
        logger.debug("taskUUID = [" + taskUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM taskworkerconstraint WHERE taskuuid LIKE :taskuuid")
                    .addParameter("taskuuid", taskUUID)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            logger.error("LOG00520:", e);
        }
        return new ArrayList<>();
    }

    public Map<String, Object> findByTaskUUIDAndUserUUID(String taskUUID, String userUUID) {
        logger.debug("TaskWorkerConstraintRepository.findByTaskUUIDAndUserUUID");
        logger.debug("taskUUID = [" + taskUUID + "], userUUID = [" + userUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            List<Map<String, Object>> maps = getEntitiesFromMapSet(con.createQuery("SELECT * FROM taskworkerconstraint WHERE taskuuid LIKE :taskuuid AND useruuid LIKE :useruuid")
                    .addParameter("taskuuid", taskUUID)
                    .addParameter("useruuid", userUUID)
                    .executeAndFetchTable().asList());

            if (maps.size() > 0) return maps.get(0);

            Map<String, Object> result = new HashMap<>();
            result.put("price", 0.0);
            result.put("taskuuid", taskUUID);
            result.put("useruuid", userUUID);
            result.put("uuid", "");
            return result;
        } catch (Exception e) {
            logger.error("LOG00530:", e);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("price", 0.0);
        result.put("taskuuid", taskUUID);
        result.put("useruuid", userUUID);
        result.put("uuid", "");
        return result;
    }

    public void create(JsonNode jsonNode) throws SQLException {
        logger.debug("TaskWorkerConstraintRepository.create");
        logger.debug("jsonNode = [" + jsonNode + "]");
        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("INSERT INTO taskworkerconstraint (uuid, price, taskuuid, useruuid) VALUES (:uuid, :price, :taskuuid, :useruuid)")
                    .addParameter("uuid", jsonNode.get("uuid").asText(UUID.randomUUID().toString()))
                    .addParameter("price", jsonNode.get("price").asDouble(0.0))
                    .addParameter("taskuuid", jsonNode.get("taskuuid").asText())
                    .addParameter("useruuid", jsonNode.get("useruuid").asText())
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00540:", e);
        }
    }

    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        logger.debug("TaskWorkerConstraintRepository.update");
        logger.debug("jsonNode = [" + jsonNode + "], uuid = [" + uuid + "]");

        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("UPDATE taskworkerconstraint SET price = :price WHERE uuid LIKE :uuid")
                    .addParameter("uuid", jsonNode.get("uuid").asText(UUID.randomUUID().toString()))
                    .addParameter("price", jsonNode.get("price").asDouble(0.0))
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00550:", e);
        }
    }
}
