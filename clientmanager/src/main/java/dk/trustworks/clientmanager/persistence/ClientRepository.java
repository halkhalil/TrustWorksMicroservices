package dk.trustworks.clientmanager.persistence;

import dk.trustworks.clientmanager.model.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
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
public class ClientRepository {

    private static final Logger logger = LogManager.getLogger();
    private final Sql2o sql2o;

    public ClientRepository(DataSource ds) {
        sql2o = new Sql2o(ds);
    }

    public List<Client> findAll() {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM client")
                    .executeAndFetch(Client.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Client findByUUID(String uuid) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM client WHERE uuid LIKE :uuid")
                    .addParameter("uuid", uuid)
                    .executeAndFetchFirst(Client.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Client();
    }

    public List<Client> findByActiveTrue() {
        logger.debug("ClientRepository.findByActiveTrue");
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM client WHERE active = TRUE ORDER BY name ASC")
                    .executeAndFetch(Client.class);
        } catch (Exception e) {
            logger.error("LOG00310:", e);
        }
        return new ArrayList<>();
    }

    public void create(Client client) throws SQLException {
        logger.debug("ClientRepository.create");
        client.uuid = UUID.randomUUID().toString();
        client.created = DateTime.now();
        client.active = true;
        try (Connection con = sql2o.open()) {
            con.createQuery("INSERT INTO client (uuid, active, contactname, created, name) VALUES (:uuid, :active, :contactname, :created, :name)")
                    .bind(client)
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00330:", e);
        }
    }

    public void update(Client client, String uuid) throws SQLException {
        client.uuid = uuid;
        try (Connection con = sql2o.open()) {
            con.createQuery("UPDATE client SET active = :active, contactname = :contactname, name = :name WHERE uuid LIKE :uuid")
                    .bind(client)
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00340:", e);
        }
    }
}
