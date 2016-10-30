package dk.trustworks.usermanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dk.trustworks.usermanager.dto.Role;
import dk.trustworks.usermanager.dto.User;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by hans on 17/03/15.
 */
public class RoleRepository {

    private final Sql2o sql2o;

    public RoleRepository(DataSource ds) {
        sql2o = new Sql2o(ds);
    }

    public List<String> findByUserUUID(String useruuid) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT role FROM roles WHERE useruuid LIKE :useruuid").addParameter("useruuid", useruuid).executeAndFetch(String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void create(Role role) throws SQLException {

    }

    public void update(JsonNode jsonNode, String uuid) throws SQLException {

    }
}
