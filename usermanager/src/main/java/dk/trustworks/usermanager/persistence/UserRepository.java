package dk.trustworks.usermanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dk.trustworks.usermanager.dto.User;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by hans on 17/03/15.
 */
public class UserRepository {

    private final Sql2o sql2o;

    Cache<String, List<User>> activeUsersCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(10, TimeUnit.MINUTES).build();

    public UserRepository(DataSource ds) {
        sql2o = new Sql2o(ds);
    }

    public List<User> findAll() {
        System.out.println("findAll()");
        try {
            return activeUsersCache.get("allActive", () -> {
                try (Connection con = sql2o.open()) {
                    return con.createQuery("SELECT uuid, username, firstname, lastname, email, created FROM user u RIGHT JOIN ( " +
                            "select t.useruuid, t.status, t.statusdate, t.allocation " +
                            "from userstatus t " +
                            "inner join ( " +
                            "select useruuid, status, max(statusdate) as MaxDate " +
                            "from userstatus " +
                            "group by useruuid " +
                            ") " +
                            "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate " +
                            ") usi ON u.uuid = usi.useruuid;").executeAndFetch(User.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new ArrayList<>();
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public User findByUUID(String uuid) {
        System.out.println("uuid = " + uuid);
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT uuid, username, firstname, lastname, email, created FROM user WHERE uuid LIKE :uuid").addParameter("uuid", uuid).executeAndFetch(User.class).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new User();
    }

    public List<User> findByActiveTrue() {
        try {
            return activeUsersCache.get("allActive", () -> {
                try (Connection con = sql2o.open()) {
                    return con.createQuery("SELECT uuid, username, firstname, lastname, email, created FROM user u RIGHT JOIN ( " +
                            "select t.useruuid, t.status, t.statusdate, t.allocation " +
                            "from userstatus t " +
                            "inner join ( " +
                            "select useruuid, status, max(statusdate) as MaxDate " +
                            "from userstatus " +
                            "group by useruuid " +
                            ") " +
                            "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate " +
                            ") usi ON u.uuid = usi.useruuid WHERE usi.status LIKE 'ACTIVE' OR usi.status LIKE 'NON_PAY_LEAVE';").executeAndFetch(User.class);
                } catch (Exception e) {

                }
                return new ArrayList<>();
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> findByActiveTrueOrderByFirstnameAsc() {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT uuid, username, firstname, lastname, email, created FROM user u RIGHT JOIN ( " +
                    "select t.useruuid, t.status, t.statusdate, t.allocation " +
                    "from userstatus t " +
                    "inner join ( " +
                    "select useruuid, status, max(statusdate) as MaxDate " +
                    "from userstatus " +
                    "group by useruuid " +
                    ") " +
                    "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate " +
                    ") usi ON u.uuid = usi.useruuid WHERE usi.status LIKE 'ACTIVE' OR usi.status LIKE 'NON_PAY_LEAVE' ORDER BY firstname ASC;").executeAndFetch(User.class);
        } catch (Exception e) {

        }
        return new ArrayList<>();
    }

    public User findByEmail(String email) {
        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery("SELECT uuid, username, firstname, lastname, email, created FROM user u RIGHT JOIN ( " +
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
                    .executeAndFetch(User.class).get(0);
        } catch (Exception e) {
        }
        return null;
    }

    public User findByUsername(String username) {
        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery("SELECT uuid, username, firstname, lastname, email, created FROM user u RIGHT JOIN ( " +
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
                    .executeAndFetch(User.class).get(0);
        } catch (Exception e) {
        }
        return null;
    }

    public User findByUsernameAndPasswordAndActiveTrue(String username, String password) {
        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery("SELECT uuid, username, firstname, lastname, email, created FROM user u RIGHT JOIN ( " +
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
                    .executeAndFetch(User.class).get(0);
        } catch (Exception e) {
        }
        return null;
    }

    public int calculateCapacityByMonth(LocalDate monthDate) {
        DateTime toDate = monthDate.toDateTimeAtStartOfDay();
        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery("SELECT SUM(allocation) capacity FROM user u RIGHT JOIN ( " +
                    "select t.useruuid, t.status, t.statusdate, t.allocation " +
                    "from userstatus t " +
                    "inner join ( " +
                    "select useruuid, status, max(statusdate) as MaxDate " +
                    "from userstatus " +
                    "WHERE statusdate <= :toDate " +
                    "group by useruuid " +
                    ") " +
                    "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate " +
                    ") usi ON u.uuid = usi.useruuid;")
                    .addParameter("toDate", toDate)
                    .executeScalar(Integer.class);
        } catch (Exception e) {
        }
        return 0;
    }

    public int calculateCapacityByMonthByUser(LocalDate monthDate, String userUUID) {
        DateTime toDate = monthDate.toDateTimeAtStartOfDay();
        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery("SELECT allocation FROM user u RIGHT JOIN ( " +
                    "select t.useruuid, t.status, t.statusdate, t.allocation " +
                    "from userstatus t " +
                    "inner join ( " +
                    "select useruuid, status, max(statusdate) as MaxDate " +
                    "from userstatus  WHERE statusdate < :toDate " +
                    "group by useruuid " +
                    ") " +
                    "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate " +
                    ") usi ON u.uuid = usi.useruuid WHERE u.uuid LIKE :useruuid;")
                    .addParameter("toDate", toDate)
                    .addParameter("useruuid", userUUID)
                    .executeScalar(Integer.class);
        } catch (Exception e) {
        }
        return 0;
    }

    public List<String> getAvailabilityByMonth(LocalDate monthDate) {
        DateTime toDate = monthDate.toDateTimeAtStartOfDay();
        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery("SELECT uuid FROM user u RIGHT JOIN ( " +
                    "select t.useruuid, t.status, t.statusdate, t.allocation " +
                    "from userstatus t " +
                    "inner join ( " +
                    "select useruuid, status, max(statusdate) as MaxDate " +
                    "from userstatus " +
                    "WHERE statusdate <= :toDate " +
                    "group by useruuid ) " +
                    "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate and t.status LIKE 'ACTIVE' " +
                    ") usi ON u.uuid = usi.useruuid;")
                    .addParameter("toDate", toDate)
                    .executeAndFetch(String.class);
        } catch (Exception e) {
        }
        return new ArrayList<>();
    }

    public List<String> getAvailabilityByMonthAndUser(String userUUID, LocalDate monthDate) {
        DateTime toDate = monthDate.toDateTimeAtStartOfDay();
        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery("SELECT uuid FROM user u RIGHT JOIN ( " +
                    "select t.useruuid, t.status, t.statusdate, t.allocation " +
                    "from userstatus t " +
                    "inner join ( " +
                    "select useruuid, status, max(statusdate) as MaxDate " +
                    "from userstatus " +
                    "WHERE statusdate <= :toDate " +
                    "group by useruuid ) " +
                    "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate and t.status LIKE 'ACTIVE' " +
                    ") usi ON u.uuid = usi.useruuid WHERE u.uuid LIKE :useruuid;")
                    .addParameter("toDate", toDate)
                    .addParameter("useruuid", userUUID)
                    .executeAndFetch(String.class);
        } catch (Exception e) {
        }
        return new ArrayList<>();
    }

    public void create(User user) throws SQLException {
        try (org.sql2o.Connection con = sql2o.open()) {
            con.createQuery("INSERT INTO user (uuid, active, created, email, firstname, lastname, password, username)" +
                    " VALUES (:UUID, :active, :created, :email, :firstname, :lastname, :password, :username)")
                    .bind(user)
                    /*
                    .addParameter("uuid", jsonNode.get("uuid").asText())
                    .addParameter("active", jsonNode.get("active").asText())
                    .addParameter("created", new Date())
                    .addParameter("email", jsonNode.get("email").asText())
                    .addParameter("firstname", jsonNode.get("firstname").asText())
                    .addParameter("lastname", jsonNode.get("lastname").asText())
                    .addParameter("password", jsonNode.get("password").asText())
                    .addParameter("username", jsonNode.get("username").asText())
                    */
                    .executeUpdate();
        } catch (Exception e) {
        }
        /*
        try (org.sql2o.Connection con = sql2o.open()) {
            con.createQuery("INSERT INTO userstatus (uuid, useruuid, status, statusdate, allocation)" +
                    " VALUES (:uuid, :useruuid, :status, :statusdate, :allocation)")
                    .bind(user.getUserstatus())
                    /*
                    .addParameter("uuid", UUID.randomUUID().toString())
                    .addParameter("useruuid", jsonNode.get("uuid").asText())
                    .addParameter("status", jsonNode.get("status").asText())
                    .addParameter("statusdate", new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(jsonNode.get("statusdate").asText()).getTime()))
                    .addParameter("allocation", jsonNode.get("allocation").asText())
                    *//*
                    .executeUpdate();
        } catch (Exception e) {
        }
    */
    }

    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        try (org.sql2o.Connection con = sql2o.open()) {
            con.createQuery("UPDATE user u SET u.email = :email, u.firstname = :firstname, u.lastname = :lastname WHERE u.uuid LIKE :uuid")
                    .addParameter("email", jsonNode.get("email").asText())
                    .addParameter("firstname", jsonNode.get("firstname").asText())
                    .addParameter("lastname", jsonNode.get("lastname").asText())
                    .addParameter("uuid", uuid)
                    .executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (org.sql2o.Connection con = sql2o.open()) {
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
