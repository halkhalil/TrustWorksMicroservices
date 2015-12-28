package dk.trustworks.invoicemanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.persistence.GenericRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by hans on 17/03/15.
 */
public class ProductLineRepository extends GenericRepository {

    private static final Logger log = LogManager.getLogger(ProductLineRepository.class);

    public ProductLineRepository() {
        super();
    }

    public void create(JsonNode jsonNode) throws SQLException {
        log.debug("ProductLineRepository.create");
        log.debug("jsonNode = [" + jsonNode + "]");
        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("INSERT INTO productline (uuid, invoiceuuid, description, rate, amount) VALUES (:uuid, :invoiceuuid, :description, :rate, :amount)")
                    .addParameter("uuid", jsonNode.get("uuid").asText(UUID.randomUUID().toString()))
                    .addParameter("invoiceuuid", jsonNode.get("invoiceuuid").asText())
                    .addParameter("description", jsonNode.get("description").asText(""))
                    .addParameter("rate", jsonNode.get("rate").asDouble())
                    .addParameter("amount", jsonNode.get("amount").asDouble())
                    .executeUpdate();
        } catch (Exception e) {
            log.error("LOG00850:", e);
        }
    }

    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        log.debug("ProductLineRepository.update");
        log.debug("jsonNode = [" + jsonNode + "], uuid = [" + uuid + "]");
        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("UPDATE productline pl SET pl.description = :description, pl.rate = :rate, pl.amount = :amount WHERE pl.uuid LIKE :uuid")
                    .addParameter("description", jsonNode.get("description").asText())
                    .addParameter("rate", jsonNode.get("rate").asDouble())
                    .addParameter("amount", jsonNode.get("amount").asDouble())
                    .executeUpdate();
        } catch (Exception e) {
            log.error("LOG00860:", e);
        }
    }
}
