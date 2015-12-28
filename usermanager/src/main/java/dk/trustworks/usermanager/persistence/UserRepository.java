package dk.trustworks.usermanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dk.trustworks.framework.persistence.GenericRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Created by hans on 17/03/15.
 */
public class UserRepository extends GenericRepository {

    private static final Logger log = LogManager.getLogger(UserRepository.class);

    Cache<String, List<Map<String, Object>>> activeUsersCache = CacheBuilder.newBuilder().maximumSize(100).build();

    public UserRepository() {
        super();
    }

    public List<Map<String, Object>> findByActiveTrue() {
        log.debug("UserRepository.findByActiveTrue");
        try {
            return activeUsersCache.get("allActive", new Callable<List<Map<String, Object>>>() {
                @Override
                public List<Map<String, Object>> call() {
                    try (org.sql2o.Connection con = database.open()) {
                        return getEntitiesFromMapSet(con.createQuery("SELECT * FROM user WHERE active = TRUE").executeAndFetchTable().asList());
                    } catch (Exception e) {
                        log.error("LOG00720:", e);
                    }
                    //return doThingsTheHardWay(key);
                    return new ArrayList<>();
                }
            });
        } catch (ExecutionException e) {
            log.error("LOG00860:", e);
            throw new RuntimeException(e);
        }
        /*
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM user WHERE active = TRUE").executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00720:", e);
        }
        */

        /*
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Connection connection = database.getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE active = TRUE", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = stmt.executeQuery();
            result = getEntitiesFromResultSet(resultSet);
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
        */
    }

    public List<Map<String, Object>> findByActiveTrueOrderByFirstnameAsc() {
        log.debug("UserRepository.findByActiveTrueOrderByFirstnameAsc");
        try (org.sql2o.Connection con = database.open()) {
            return getEntitiesFromMapSet(con.createQuery("SELECT * FROM user WHERE active = TRUE ORDER BY firstname ASC").executeAndFetchTable().asList());
        } catch (Exception e) {
            log.error("LOG00730:", e);
        }
        return new ArrayList<>();
        /*
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Connection connection = database.getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE active = TRUE ORDER BY firstname ASC", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = stmt.executeQuery();
            result = getEntitiesFromResultSet(resultSet);
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
        */
    }

    public Map<String, Object> findByEmail(String email) {
        log.debug("UserRepository.findByEmail");
        log.debug("email = [" + email + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntityFromMap(con.createQuery("SELECT * FROM user WHERE email LIKE :email")
                    .addParameter("email", email)
                    .executeAndFetchTable().asList().get(0));
        } catch (Exception e) {
            log.error("LOG00760:", e);
        }
        return null;
        /*
        Map<String, Object> result = new HashMap<>();
        try {
            Connection connection = database.getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE email LIKE ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            result = getEntityFromResultSet(resultSet);
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
        */
    }

    public Map<String, Object> findByUsername(String username) {
        log.debug("UserRepository.findByUsername");
        log.debug("username = [" + username + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntityFromMap(con.createQuery("SELECT * FROM user WHERE username LIKE :username")
                    .addParameter("username", username)
                    .executeAndFetchTable().asList().get(0));
        } catch (Exception e) {
            log.error("LOG00750:", e);
        }
        return null;
        /*
        Map<String, Object> result = new HashMap<>();
        try {
            Connection connection = database.getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE username LIKE ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            result = getEntityFromResultSet(resultSet);
            resultSet.close();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
        */
    }

    public Map<String, Object> findByUsernameAndPasswordAndActiveTrue(String username, String password) {
        log.debug("UserRepository.findByUsernameAndPasswordAndActiveTrue");
        log.debug("username = [" + username + "], password = [" + password + "]");
        try (org.sql2o.Connection con = database.open()) {
            return getEntityFromMap(con.createQuery("SELECT * FROM user WHERE username LIKE :username AND password LIKE :password AND active = TRUE")
                    .addParameter("username", username)
                    .addParameter("password", password)
                    .executeAndFetchTable().asList().get(0));
        } catch (Exception e) {
            log.error("LOG00770:", e);
        }
        return null;
        /*
        Map<String, Object> result = new HashMap<>();
        try {
            Connection connection = database.getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE username LIKE ? AND password LIKE ? AND active = TRUE", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            result = getEntityFromResultSet(resultSet);
            resultSet.close();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
        */
    }

    @Override
    public void create(JsonNode clientJsonNode) throws SQLException {

    }

    @Override
    public void update(JsonNode clientJsonNode, String uuid) throws SQLException {

    }
}
