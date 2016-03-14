package dk.trustworks.usermanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.persistence.GenericRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.sql2o.Connection;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hans on 17/03/15.
 */
public class SalaryRepository extends GenericRepository {

    private static final Logger log = LogManager.getLogger(SalaryRepository.class);

    public SalaryRepository() {
        super();
    }

    public List<Map<String, Object>> findActiveByDate(DateTime date) {
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT uuid, useruuid, salary FROM user u RIGHT JOIN ( " +
                    "select t.useruuid, t.salary, t.activefrom " +
                    "from salary t " +
                    "inner join ( " +
                    "select useruuid, salary, max(activefrom) as MaxDate " +
                    "from salary " +
                    "WHERE activefrom <= :date " +
                    "group by useruuid ) " +
                    "tm on t.useruuid = tm.useruuid and t.activefrom = tm.MaxDate " +
                    ") usi ON u.uuid = usi.useruuid;")
                    .addParameter("date", date)
                    .executeAndFetchTable().asList());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("LOG00778:", e);
        }
        return new ArrayList<>();
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        try (Connection con = database.open()) {
            con.createQuery("INSERT INTO user (uuid, active, created, email, firstname, lastname, password, username)" +
                    " VALUES (:uuid, :active, :created, :email, :firstname, :lastname, :password, :username)")
                    .addParameter("uuid", jsonNode.get("uuid").asText())
                    .addParameter("active", jsonNode.get("active").asText())
                    .addParameter("created", new Date())
                    .addParameter("email", jsonNode.get("email").asText())
                    .addParameter("firstname", jsonNode.get("firstname").asText())
                    .addParameter("lastname", jsonNode.get("lastname").asText())
                    .addParameter("password", jsonNode.get("password").asText())
                    .addParameter("username", jsonNode.get("username").asText())
                    .executeUpdate();
        } catch (Exception e) {
            log.error("LOG00600:", e);
        }
        try (Connection con = database.open()) {
            con.createQuery("INSERT INTO userstatus (uuid, useruuid, status, statusdate, allocation)" +
                    " VALUES (:uuid, :useruuid, :status, :statusdate, :allocation)")
                    .addParameter("uuid", UUID.randomUUID().toString())
                    .addParameter("useruuid", jsonNode.get("uuid").asText())
                    .addParameter("status", jsonNode.get("status").asText())
                    .addParameter("statusdate", new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(jsonNode.get("statusdate").asText()).getTime()))
                    .addParameter("allocation", jsonNode.get("allocation").asText())
                    .executeUpdate();
        } catch (Exception e) {
            log.error("LOG00600:", e);
        }
    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        try (Connection con = database.open()) {
            con.createQuery("UPDATE user u SET u.email = :email, u.firstname = :firstname, u.lastname = :lastname WHERE u.uuid LIKE :uuid")
                    .addParameter("email", jsonNode.get("email").asText())
                    .addParameter("firstname", jsonNode.get("firstname").asText())
                    .addParameter("lastname", jsonNode.get("lastname").asText())
                    .addParameter("uuid", uuid)
                    .executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (Connection con = database.open()) {
            con.createQuery("INSERT INTO userstatus (uuid, useruuid, status, statusdate, allocation)" +
                    " VALUES (:uuid, :useruuid, :status, :statusdate, :allocation)")
                    .addParameter("uuid", UUID.randomUUID().toString())
                    .addParameter("useruuid", uuid)
                    .addParameter("status", jsonNode.get("status").asText())
                    .addParameter("statusdate", new java.sql.Date(new SimpleDateFormat().parse(jsonNode.get("statusdate").asText()).getTime()))
                    .addParameter("allocation", jsonNode.get("allocation").asText())
                    .executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
