package dk.trustworks.usermanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.model.Salary;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by hans on 17/03/15.
 */
public class SalaryRepository {

    private final Sql2o sql2o;

    public SalaryRepository(DataSource ds) {
        sql2o = new Sql2o(ds);
    }

    public List<Salary> findActiveByDate(LocalDate monthDate) {
        DateTime toDate = monthDate.toDateTimeAtStartOfDay();
        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery("SELECT uuid, useruuid, salary FROM user u RIGHT JOIN ( " +
                    "select t.useruuid, t.salary, t.activefrom " +
                    "from salary t " +
                    "inner join ( " +
                    "select useruuid, salary, max(activefrom) as MaxDate " +
                    "from salary " +
                    "WHERE activefrom <= :date " +
                    "group by useruuid ) " +
                    "tm on t.useruuid = tm.useruuid and t.activefrom = tm.MaxDate " +
                    ") usi ON u.uuid = usi.useruuid;")
                    .addParameter("date", toDate)
                    .executeAndFetch(Salary.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Salary> findActiveByDateAndUser(String userUUID, LocalDate monthDate) {
        DateTime toDate = monthDate.toDateTimeAtStartOfDay();
        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery("SELECT uuid, useruuid, salary FROM user u RIGHT JOIN ( " +
                    "select t.useruuid, t.salary, t.activefrom " +
                    "from salary t " +
                    "inner join ( " +
                    "select useruuid, salary, max(activefrom) as MaxDate " +
                    "from salary " +
                    "WHERE activefrom <= :date " +
                    "group by useruuid ) " +
                    "tm on t.useruuid = tm.useruuid and t.activefrom = tm.MaxDate " +
                    ") usi ON u.uuid = usi.useruuid WHERE useruuid LIKE :useruuid;")
                    .addParameter("date", toDate)
                    .addParameter("useruuid", userUUID)
                    .executeAndFetch(Salary.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void create(JsonNode jsonNode) throws SQLException {
        try (Connection con = sql2o.open()) {
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
        }
        try (Connection con = sql2o.open()) {
            con.createQuery("INSERT INTO userstatus (uuid, useruuid, status, statusdate, allocation)" +
                    " VALUES (:uuid, :useruuid, :status, :statusdate, :allocation)")
                    .addParameter("uuid", UUID.randomUUID().toString())
                    .addParameter("useruuid", jsonNode.get("uuid").asText())
                    .addParameter("status", jsonNode.get("status").asText())
                    .addParameter("statusdate", new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(jsonNode.get("statusdate").asText()).getTime()))
                    .addParameter("allocation", jsonNode.get("allocation").asText())
                    .executeUpdate();
        } catch (Exception e) {
        }
    }

    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        try (Connection con = sql2o.open()) {
            con.createQuery("UPDATE user u SET u.email = :email, u.firstname = :firstname, u.lastname = :lastname WHERE u.uuid LIKE :uuid")
                    .addParameter("email", jsonNode.get("email").asText())
                    .addParameter("firstname", jsonNode.get("firstname").asText())
                    .addParameter("lastname", jsonNode.get("lastname").asText())
                    .addParameter("uuid", uuid)
                    .executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (Connection con = sql2o.open()) {
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
