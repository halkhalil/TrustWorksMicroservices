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
public class InvoiceRepository extends GenericRepository {

    private static final Logger log = LogManager.getLogger(InvoiceRepository.class);

    public InvoiceRepository() {
        super();
    }

    public List<Map<String, Object>> findByProjectUUIDAndMonthAndYear(String projectUUID, int month, int year) throws SQLException {
        log.debug("InvoiceRepository.findByProjectUUIDAndMonthAndYear");
        log.debug("projectUUID = [" + projectUUID + "], month = [" + month + "], year = [" + year + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM invoices WHERE projectuuid LIKE :projectuuid AND month = :month AND year = :year")
                    .addParameter("projectuuid", projectUUID)
                    .addParameter("month", month)
                    .addParameter("year", year)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG01310:", e);
        }
        return new ArrayList<>();
    }

    public void create(JsonNode jsonNode) throws SQLException {
        log.debug("InvoiceRepository.create");
        log.debug("jsonNode = [" + jsonNode + "]");
        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("INSERT INTO invoice (uuid, invoicenumber, description, month, year, projectuuid, created) VALUES (:uuid, :invoicenumber, :description, :month, :year, :projectuuid, :created)")
                    .addParameter("uuid", jsonNode.get("uuid").asText(UUID.randomUUID().toString()))
                    .addParameter("projectuuid", jsonNode.get("projectuuid").asText())
                    .addParameter("month", jsonNode.get("month").asDouble())
                    .addParameter("year", jsonNode.get("year").asDouble())
                    .executeUpdate();
        } catch (Exception e) {
            log.error("LOG00850:", e);
        }
    }

    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        log.debug("InvoiceRepository.update");
        log.debug("jsonNode = [" + jsonNode + "], uuid = [" + uuid + "]");
        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("UPDATE invoice i SET i.description = :description, i.rate = :rate, i.amount = :amount WHERE i.uuid LIKE :uuid")
                    .addParameter("description", jsonNode.get("description").asText())
                    .addParameter("rate", jsonNode.get("rate").asDouble())
                    .addParameter("amount", jsonNode.get("amount").asDouble())
                    .executeUpdate();
        } catch (Exception e) {
            log.error("LOG00860:", e);
        }
    }
}