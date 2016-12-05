package dk.trustworks.usermanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.model.Role;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
