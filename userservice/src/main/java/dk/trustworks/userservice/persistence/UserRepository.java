package dk.trustworks.userservice.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dk.trustworks.framework.persistence.GenericRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.sql2o.Connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by hans on 17/03/15.
 */
public class UserRepository extends GenericRepository {

    private static final Logger log = LogManager.getLogger(UserRepository.class);

    Cache<String, List<Map<String, Object>>> activeUsersCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(10, TimeUnit.MINUTES).build();

    public UserRepository() {
        super();
    }

    public List<Map<String, Object>> findByActiveTrue() {
        log.debug("UserRepository.findByActiveTrue");
        try {
            return activeUsersCache.get("allActive", () -> {
                try (org.sql2o.Connection con = database.open()) {
                    return getEntitiesFromMapSet(con.createQuery("SELECT * FROM user u RIGHT JOIN ( " +
                            "select t.useruuid, t.status, t.statusdate, t.allocation " +
                            "from userstatus t " +
                            "inner join ( " +
                            "select useruuid, status, max(statusdate) as MaxDate " +
                            "from userstatus " +
                            "group by useruuid " +
                            ") " +
                            "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate " +
                            ") usi ON u.uuid = usi.useruuid WHERE usi.status LIKE 'ACTIVE' OR usi.status LIKE 'NON_PAY_LEAVE';").executeAndFetchTable().asList());
                } catch (Exception e) {
                    log.error("LOG00720:", e);
                }
                return new ArrayList<>();
            });
        } catch (ExecutionException e) {
            log.error("LOG00860:", e);
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, Object>> findByActiveTrueOrderByFirstnameAsc() {
        log.debug("UserRepository.findByActiveTrueOrderByFirstnameAsc");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM user u RIGHT JOIN ( " +
                    "select t.useruuid, t.status, t.statusdate, t.allocation " +
                    "from userstatus t " +
                    "inner join ( " +
                    "select useruuid, status, max(statusdate) as MaxDate " +
                    "from userstatus " +
                    "group by useruuid " +
                    ") " +
                    "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate " +
                    ") usi ON u.uuid = usi.useruuid WHERE usi.status LIKE 'ACTIVE' OR usi.status LIKE 'NON_PAY_LEAVE' ORDER BY firstname ASC;").executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00730:", e);
        }
        return new ArrayList<>();
    }

    public Map<String, Object> findByEmail(String email) {
        log.debug("UserRepository.findByEmail");
        log.debug("email = [" + email + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntityFromMap(con.createQuery("SELECT * FROM user u RIGHT JOIN ( " +
                    "select t.useruuid, t.status, t.statusdate, t.allocation " +
                    "from userstatus t " +
                    "inner join ( " +
                    "select useruuid, status, max(statusdate) as MaxDate " +
                    "from userstatus " +
                    "group by useruuid " +
                    ") " +
                    "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate " +
                    ") usi ON u.uuid = usi.useruuid WHERE u.email LIKE :email;")
                    .addParameter("email", email)
                    .executeAndFetchTable().asList().get(0));
        } catch (Exception e) {
            log.error("LOG00760:", e);
        }
        return null;
    }

    public Map<String, Object> findByUsername(String username) {
        log.debug("UserRepository.findByUsername");
        log.debug("username = [" + username + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntityFromMap(con.createQuery("SELECT * FROM user u RIGHT JOIN ( " +
                    "select t.useruuid, t.status, t.statusdate, t.allocation " +
                    "from userstatus t " +
                    "inner join ( " +
                    "select useruuid, status, max(statusdate) as MaxDate " +
                    "from userstatus " +
                    "group by useruuid " +
                    ") " +
                    "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate " +
                    ") usi ON u.uuid = usi.useruuid WHERE u.username LIKE :username;")
                    .addParameter("username", username)
                    .executeAndFetchTable().asList().get(0));
        } catch (Exception e) {
            log.error("LOG00750:", e);
        }
        return null;
    }

    public Map<String, Object> findByUsernameAndPasswordAndActiveTrue(String username, String password) {
        log.debug("UserRepository.findByUsernameAndPasswordAndActiveTrue");
        log.debug("username = [" + username + "], password = [" + password + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntityFromMap(con.createQuery("SELECT * FROM user u RIGHT JOIN ( " +
                    "select t.useruuid, t.status, t.statusdate, t.allocation " +
                    "from userstatus t " +
                    "inner join ( " +
                    "select useruuid, status, max(statusdate) as MaxDate " +
                    "from userstatus " +
                    "group by useruuid " +
                    ") " +
                    "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate " +
                    ") usi ON u.uuid = usi.useruuid WHERE u.username LIKE :username AND u.password LIKE :password AND (usi.status LIKE 'ACTIVE' OR usi.status LIKE 'NON_PAY_LEAVE');")
                    .addParameter("username", username)
                    .addParameter("password", password)
                    .executeAndFetchTable().asList().get(0));
        } catch (Exception e) {
            log.error("LOG00770:", e);
        }
        return null;
    }

    public int calculateCapacityByMonth(DateTime toDate) {
        try (org.sql2o.Connection con = database.open()) {
            return con.createQuery("SELECT SUM(allocation) capacity FROM user u RIGHT JOIN ( " +
                    "select t.useruuid, t.status, t.statusdate, t.allocation " +
                    "from userstatus t " +
                    "inner join ( " +
                    "select useruuid, status, max(statusdate) as MaxDate " +
                    "from userstatus " +
                    "WHERE statusdate < :toDate " +
                    "group by useruuid " +
                    ") " +
                    "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate " +
                    ") usi ON u.uuid = usi.useruuid;")
                    .addParameter("toDate", toDate)
                    .executeScalar(Integer.class);
        } catch (Exception e) {
            log.error("LOG00771:", e);
        }
        return 0;
    }

    @Override
    public List<Map<String, Object>> getAllEntities(String entityName) {
        try (Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM user u RIGHT JOIN ( " +
                    "select t.useruuid, t.status, t.statusdate, t.allocation " +
                    "from userstatus t " +
                    "inner join ( " +
                    "select useruuid, status, max(statusdate) as MaxDate " +
                    "from userstatus " +
                    "group by useruuid " +
                    ") " +
                    "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate " +
                    ") usi ON u.uuid = usi.useruuid;").executeAndFetchTable().asList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        try (org.sql2o.Connection con = database.open()) {
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
        try (org.sql2o.Connection con = database.open()) {
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
        try (org.sql2o.Connection con = database.open()) {
            con.createQuery("UPDATE user u SET u.email = :email, u.firstname = :firstname, u.lastname = :lastname WHERE u.uuid LIKE :uuid")
                    .addParameter("email", jsonNode.get("email").asText())
                    .addParameter("firstname", jsonNode.get("firstname").asText())
                    .addParameter("lastname", jsonNode.get("lastname").asText())
                    .addParameter("uuid", uuid)
                    .executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (org.sql2o.Connection con = database.open()) {
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
