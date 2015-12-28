package dk.trustworks.invoicemanager.persistence;

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
public class ClientDataRepository extends GenericRepository {

    private static final Logger logger = LogManager.getLogger();

    public ClientDataRepository() {
        super();
    }

    public List<Map<String, Object>> findByClientUUID(String clientUUID) {
        logger.debug("ClientDataRepository.findByClientUUID");
        logger.debug("clientUUID = [" + clientUUID + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM clientdata WHERE clientuuid LIKE :clientuuid")
                    .addParameter("clientuuid", clientUUID)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            logger.error("LOG00280:", e);
        }
        return new ArrayList<>();
    }

    public void create(JsonNode jsonNode) throws SQLException {
        logger.debug("ClientDataRepository.create");
        logger.debug("jsonNode = [" + jsonNode + "]");
        testForNull(jsonNode, new String[]{"clientuuid", "clientname"});
        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("INSERT INTO clientdata (uuid, city, clientuuid, clientname, contactperson, cvr, ean, otheraddressinfo, postalcode, streetnamenumber)" +
                    " VALUES (:uuid, :city, :clientuuid, :clientname, :contactperson, :cvr, :ean, :otheraddressinfo, :postalcode, :streetnamenumber)")
                    .addParameter("uuid", jsonNode.get("uuid").asText(UUID.randomUUID().toString()))
                    .addParameter("city", jsonNode.get("city").asText())
                    .addParameter("clientuuid", jsonNode.get("clientuuid").asText())
                    .addParameter("clientname", jsonNode.get("clientname").asText())
                    .addParameter("contactperson", jsonNode.get("contactperson").asText(""))
                    .addParameter("cvr", jsonNode.get("cvr").asText(""))
                    .addParameter("ean", jsonNode.get("ean").asText(""))
                    .addParameter("otheraddressinfo", jsonNode.get("otheraddressinfo").asText())
                    .addParameter("postalcode", jsonNode.get("postalcode").asInt(0))
                    .addParameter("streetnamenumber", jsonNode.get("streetnamenumber").asText(""))
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00290:", e);
        }
    }

    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        logger.debug("ClientDataRepository.update");
        logger.debug("jsonNode = [" + jsonNode + "], uuid = [" + uuid + "]");
        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("UPDATE clientdata SET city = :city, clientname = :clientname, contactperson = :contactperson, cvr = :cvr, ean = :ean, otheraddressinfo = :otheraddressinfo, " +
                    "postalcode = :postalcode, streetnamenumber  = :streetnamenumber WHERE uuid LIKE :uuid")
                    .addParameter("city", jsonNode.get("city").asText())
                    .addParameter("clientname", jsonNode.get("clientname").asText())
                    .addParameter("contactperson", jsonNode.get("contactperson").asText())
                    .addParameter("cvr", jsonNode.get("cvr").asText())
                    .addParameter("ean", jsonNode.get("ean").asText())
                    .addParameter("otheraddressinfo", jsonNode.get("otheraddressinfo").asText())
                    .addParameter("postalcode", jsonNode.get("postalcode").asInt(0))
                    .addParameter("streetnamenumber", jsonNode.get("streetnamenumber").asText())
                    .addParameter("uuid", jsonNode.get("uuid").asText())
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00300:", e);
        }
    }
}
