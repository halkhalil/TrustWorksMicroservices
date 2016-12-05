package dk.trustworks.clientmanager.persistence;

import dk.trustworks.framework.model.ClientData;
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
public class ClientDataRepository {

    private static final Logger logger = LogManager.getLogger();
    private final Sql2o sql2o;

    public ClientDataRepository(DataSource ds) {
        sql2o = new Sql2o(ds);
    }

    public List<ClientData> findAll() {
        try (Connection con = sql2o.open()) {
            List<ClientData> clientDatas = con.createQuery("SELECT * FROM clientdata")
                    .executeAndFetch(ClientData.class);
            con.close();
            return clientDatas;
        } catch (Exception e) {
            logger.error("LOG00280:", e);
        }
        return new ArrayList<>();
    }

    public ClientData findByUUID(String uuid) {
        try (Connection con = sql2o.open()) {
            ClientData clientData = con.createQuery("SELECT * FROM clientdata WHERE uuid LIKE :uuid")
                    .addParameter("uuid", uuid)
                    .executeAndFetchFirst(ClientData.class);
            con.close();
            return clientData;
        } catch (Exception e) {
            logger.error("LOG00280:", e);
        }
        return null;
    }

    public List<ClientData> findByClientUUID(String clientUUID) {
        logger.debug("ClientDataRepository.findByClientUUID");
        logger.debug("clientUUID = [" + clientUUID + "]");
        try (Connection con = sql2o.open()) {
            List<ClientData> clientDatas = con.createQuery("SELECT * FROM clientdata WHERE clientuuid LIKE :clientuuid")
                    .addParameter("clientuuid", clientUUID)
                    .executeAndFetch(ClientData.class);
            con.close();
            return clientDatas;
        } catch (Exception e) {
            logger.error("LOG00280:", e);
        }
        return new ArrayList<>();
    }

    public ClientData findByProjectUUID(String projectUUID) {
        try (Connection con = sql2o.open()) {
            ClientData clientData = con.createQuery("SELECT cd.uuid, cd.city, cd.clientuuid, cd.clientname, cd.contactperson, cd.cvr, cd.ean, cd.otheraddressinfo, cd.postalcode, cd.streetnamenumber " +
                    "FROM project p " +
                    "INNER JOIN clientdata cd " +
                    "ON p.clientdatauuid = cd.uuid " +
                    "WHERE p.uuid LIKE :projectuuid")
                    .addParameter("projectuuid", projectUUID)
                    .executeAndFetchFirst(ClientData.class);
            con.close();
            return clientData;
        } catch (Exception e) {
            logger.error("LOG00280:", e);
        }
        return new ClientData();
    }

    public ClientData create(ClientData clientData) throws SQLException {
        logger.debug("ClientDataRepository.create");
        clientData.uuid = UUID.randomUUID().toString();

        try (Connection con = sql2o.open()) {
            con.createQuery("INSERT INTO clientdata (uuid, city, clientuuid, clientname, contactperson, cvr, ean, otheraddressinfo, postalcode, streetnamenumber)" +
                    " VALUES (:uuid, :city, :clientuuid, :clientname, :contactperson, :cvr, :ean, :otheraddressinfo, :postalcode, :streetnamenumber)")
                    .bind(clientData)
                    .executeUpdate();
            con.close();
        } catch (Exception e) {
            logger.error("LOG00290:", e);
        }
        return clientData;
    }

    public void update(ClientData clientData, String uuid) throws SQLException {
        logger.debug("ClientDataRepository.update");
        clientData.uuid = uuid;

        try (Connection con = sql2o.open()) {
            con.createQuery("UPDATE clientdata SET city = :city, clientname = :clientname, contactperson = :contactperson, cvr = :cvr, ean = :ean, otheraddressinfo = :otheraddressinfo, " +
                    "postalcode = :postalcode, streetnamenumber  = :streetnamenumber WHERE uuid LIKE :uuid")
                    .bind(clientData)
                    .executeUpdate();
            con.close();
        } catch (Exception e) {
            logger.error("LOG00300:", e);
        }
    }
}
