package dk.trustworks.clientmanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.persistence.GenericRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hans on 17/03/15.
 */
public class ClientRepository extends GenericRepository {

    private static final Logger logger = LogManager.getLogger();

    public ClientRepository() {
        super();
    }

    public List<Map<String, Object>> findByActiveTrue() {
        logger.debug("ClientRepository.findByActiveTrue");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM client WHERE active = TRUE ORDER BY name ASC")
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            logger.error("LOG00310:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByActiveTrueOrderByNameAsc() {
        logger.debug("ClientRepository.findByActiveTrueOrderByNameAsc");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM client WHERE active = TRUE ORDER BY name ASC")
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            logger.error("LOG00320:", e);
        }
        return new ArrayList<>();
    }

    public void create(JsonNode jsonNode) throws SQLException {
        logger.debug("ClientRepository.create");
        logger.debug("jsonNode = [" + jsonNode + "]");
        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("INSERT INTO client (uuid, active, contactname, created, name) VALUES (:uuid, :active, :contactname, :created, :name)")
                    .addParameter("uuid", jsonNode.get("uuid").asText(UUID.randomUUID().toString()))
                    .addParameter("active", true)
                    .addParameter("contactname", jsonNode.get("contactname").asText(""))
                    .addParameter("created", new Date(new java.util.Date().getTime()))
                    .addParameter("name", jsonNode.get("name").asText(""))
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00330:", e);
        }
    }

    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        logger.debug("Update client: " + jsonNode);
        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("UPDATE client SET active = :active, contactname = :contactname, name = :name WHERE uuid LIKE :uuid")
                    .addParameter("uuid", jsonNode.get("uuid").asText(UUID.randomUUID().toString()))
                    .addParameter("active", jsonNode.get("active").asBoolean())
                    .addParameter("contactname", jsonNode.get("contactname").asText())
                    .addParameter("name", jsonNode.get("name").asText())
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00340:", e);
        }
    }
}
