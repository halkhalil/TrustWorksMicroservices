package dk.trustworks.clientmanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.persistence.GenericRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hans on 17/03/15.
 */
public class ProjectRepository extends GenericRepository {

    private static final Logger logger = LogManager.getLogger();

    public ProjectRepository() {
        super();
    }

    public List<Map<String, Object>> findByOrderByNameAsc() {
        logger.debug("ProjectRepository.findByOrderByNameAsc");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM project ORDER BY name ASC").executeAndFetchTable().asList());
        } catch (Exception e) {
            logger.error("LOG00350:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByActiveTrueOrderByNameAsc() {
        logger.debug("ProjectRepository.findByActiveTrueOrderByNameAsc");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM project WHERE active = TRUE ORDER BY name ASC").executeAndFetchTable().asList());
        } catch (Exception e) {
            logger.error("LOG00360:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByActiveFalseOrderByNameAsc() {
        logger.debug("ProjectRepository.findByActiveFalseOrderByNameAsc");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM project WHERE active = FALSE ORDER BY name ASC").executeAndFetchTable().asList());
        } catch (Exception e) {
            logger.error("LOG00870:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByClientUUID(String clientUUID) {
        logger.debug("ProjectRepository.findByClientUUID");
        logger.debug("clientUUID = [" + clientUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM project WHERE clientuuid LIKE :clientuuid").addParameter("clientuuid", clientUUID).executeAndFetchTable().asList());
        } catch (Exception e) {
            logger.error("LOG00370:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByClientUUIDAndActiveTrue(String clientUUID) {
        logger.debug("ProjectRepository.findByClientUUIDAndActiveTrue");
        logger.debug("clientUUID = [" + clientUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM project WHERE clientuuid LIKE :clientuuid AND active = TRUE").addParameter("clientuuid", clientUUID).executeAndFetchTable().asList());
        } catch (Exception e) {
            logger.error("LOG00380:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByClientUUIDOrderByNameAsc(String clientUUID) {
        logger.debug("ProjectRepository.findByClientUUIDOrderByNameAsc");
        logger.debug("clientUUID = [" + clientUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM project WHERE clientuuid LIKE :clientuuid ORDER BY name ASC").addParameter("clientuuid", clientUUID).executeAndFetchTable().asList());
        } catch (Exception e) {
            logger.error("LOG00390:", e);
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findByClientUUIDAndActiveTrueOrderByNameAsc(String clientUUID) {
        logger.debug("ProjectRepository.findByClientUUIDAndActiveTrueOrderByNameAsc");
        logger.debug("clientUUID = [" + clientUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM project WHERE clientuuid LIKE :clientuuid AND active = TRUE ORDER BY name ASC").addParameter("clientuuid", clientUUID).executeAndFetchTable().asList());
        } catch (Exception e) {
            logger.error("LOG00400:", e);
        }
        return new ArrayList<>();
    }

    public void create(JsonNode jsonNode) {
        logger.info("ProjectRepository.create");
        logger.info("jsonNode = [" + jsonNode + "]");
        testForNull(jsonNode, new String[]{"clientuuid", "clientdatauuid"});
        try (org.sql2o.Connection con = database.open()) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 4);
            con.createQuery("INSERT INTO project (uuid, active, budget, clientuuid, created, customerreference, name, userowneruuid, clientdatauuid, startdate, enddate) VALUES (:uuid, :active, :budget, :clientuuid, :created, :customerreference, :name, :userowneruuid, :clientdatauuid, :startdate, :enddate)")
                    .addParameter("uuid", jsonNode.get("uuid").asText(UUID.randomUUID().toString()))
                    .addParameter("active", jsonNode.get("active").asBoolean(true))
                    .addParameter("budget", jsonNode.get("budget").asDouble(0.0))
                    .addParameter("clientuuid", jsonNode.get("clientuuid").asText())
                    .addParameter("created", new Date(new java.util.Date().getTime()))
                    .addParameter("customerreference", jsonNode.get("customerreference").asText())
                    .addParameter("name", jsonNode.get("name").asText())
                    .addParameter("userowneruuid", jsonNode.get("userowneruuid").asText())
                    .addParameter("clientdatauuid", jsonNode.get("clientdatauuid").asText())
                    .addParameter("startdate", (jsonNode.get("startdate").asText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()))))
                    .addParameter("enddate", (jsonNode.get("enddate").asText(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()))))
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00410:", e);
        }
    }

    public void update(JsonNode jsonNode, String uuid) {
        logger.info("ProjectRepository.update");
        logger.info("jsonNode = [" + jsonNode + "], uuid = [" + uuid + "]");

        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("UPDATE project p SET p.active = :active, p.budget = :budget, p.customerreference = :customerreference, p.name = :name, p.userowneruuid = :userowneruuid, p.startdate = :startdate, p.enddate = :enddate WHERE p.uuid LIKE :uuid")
                    .addParameter("active", jsonNode.get("active").asBoolean(true))
                    .addParameter("budget", jsonNode.get("budget").asDouble(0.0))
                    .addParameter("customerreference", jsonNode.get("customerreference").asText())
                    .addParameter("name", jsonNode.get("name").asText())
                    .addParameter("userowneruuid", jsonNode.get("userowneruuid").asText(""))
                    .addParameter("startdate", new Date(new SimpleDateFormat("yyyy-MM-dd").parse(jsonNode.get("startdate").asText()).getTime()))
                    .addParameter("enddate", new Date(new SimpleDateFormat("yyyy-MM-dd").parse(jsonNode.get("enddate").asText()).getTime()))
                    .addParameter("uuid", jsonNode.get("uuid").asText())
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00420:", e);
        }
    }
}
