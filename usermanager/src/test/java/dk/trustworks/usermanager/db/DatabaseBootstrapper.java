package dk.trustworks.usermanager.db;

import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

/**
 * Created by hans on 07/12/2016.
 */

public class DatabaseBootstrapper {

    private Sql2o sql2o;

    @Before
    public void setUp() throws Exception {
        sql2o = new Sql2o("jdbc:mysql://localhost:3306/usermanager", "financeuser", "Nostromo2014");
    }

    @Test
    public void insertData() throws Exception {
        String sql = "INSERT INTO usermanager.user " +
                "(uuid, active, created, email, firstname, lastname, password, username) " +
                "VALUES (:uuid, :active, :created, :email, :firstname, :lastname, :password, :username);";

        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("uuid", "7948c5e8-162c-4053-b905-0f59a21d7746")
                    .addParameter("active", true)
                    .addParameter("created", "2014-10-29 10:03:38")
                    .addParameter("email", "hans.lassen@trustworks.dk")
                    .addParameter("firstname", "Hans")
                    .addParameter("lastname", "Ernst Lassen")
                    .addParameter("password", "1234")
                    .addParameter("username", "hans.lassen")
                    .executeUpdate();

            con.createQuery(sql)
                    .addParameter("uuid", "8fa7f75a-57bf-4c6f-8db7-7e16067c1bcd")
                    .addParameter("active", true)
                    .addParameter("created", "2014-10-29 10:03:38")
                    .addParameter("email", "thomas.gammelvind@trustworks.dk")
                    .addParameter("firstname", "Thomas")
                    .addParameter("lastname", "Gammelvind")
                    .addParameter("password", "1234")
                    .addParameter("username", "thomas.gammelvind")
                    .executeUpdate();

            con.createQuery(sql)
                    .addParameter("uuid", "ade4859d-9c2f-4071-a492-d6fb8bf421ad")
                    .addParameter("active", true)
                    .addParameter("created", "2014-10-29 10:03:38")
                    .addParameter("email", "peter.gaarde@trustworks.dk")
                    .addParameter("firstname", "Peter")
                    .addParameter("lastname", "Gaarde")
                    .addParameter("password", "1234")
                    .addParameter("username", "peter.gaarde")
                    .executeUpdate();
            con.close();
        } catch (Exception e){
            e.printStackTrace();
        }


        String sqlStatus = "INSERT INTO usermanager.userstatus " +
                "(uuid, useruuid, status, statusdate, allocation) " +
                "VALUES " +
                "(:uuid, :useruuid, :status, :statusdate, :allocation);";

        try (Connection con = sql2o.open()) {
            con.createQuery(sqlStatus)
                    .addParameter("uuid", "12147210-e945-11e5-9ce9-5e5517507c66")
                    .addParameter("useruuid", "7948c5e8-162c-4053-b905-0f59a21d7746")
                    .addParameter("status", "NON_PAY_LEAVE")
                    .addParameter("statusdate", "2014-02-01")
                    .addParameter("allocation", 0)
                    .executeUpdate();

            con.createQuery(sqlStatus)
                    .addParameter("uuid", "198f02cc-e944-11e5-9ce9-5e5517507c66")
                    .addParameter("useruuid", "7948c5e8-162c-4053-b905-0f59a21d7746")
                    .addParameter("status", "ACTIVE")
                    .addParameter("statusdate", "2014-06-01")
                    .addParameter("allocation", 37)
                    .executeUpdate();

            con.createQuery(sqlStatus)
                    .addParameter("uuid", "2be24786-e944-11e5-9ce9-5e5517507c66")
                    .addParameter("useruuid", "8fa7f75a-57bf-4c6f-8db7-7e16067c1bcd")
                    .addParameter("status", "ACTIVE")
                    .addParameter("statusdate", "2015-02-01")
                    .addParameter("allocation", 37)
                    .executeUpdate();

            con.createQuery(sqlStatus)
                    .addParameter("uuid", "3f68f71e-e944-11e5-9ce9-5e5517507c66")
                    .addParameter("useruuid", "ade4859d-9c2f-4071-a492-d6fb8bf421ad")
                    .addParameter("status", "ACTIVE")
                    .addParameter("statusdate", "2014-02-01")
                    .addParameter("allocation", 30)
                    .executeUpdate();
            con.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        String sqlSalary = "INSERT INTO usermanager.salary " +
                "(uuid, useruuid, salary, activefrom) " +
                "VALUES (:uuid, :useruuid, :salary, :activefrom);";

        try (Connection con = sql2o.open()) {
            con.createQuery(sqlSalary)
                    .addParameter("uuid", "60345194-e851-11e5-9ce9-5e5517507c66")
                    .addParameter("useruuid", "7948c5e8-162c-4053-b905-0f59a21d7746")
                    .addParameter("salary", 50000)
                    .addParameter("activefrom", "2014-02-01")
                    .executeUpdate();

            con.createQuery(sqlSalary)
                    .addParameter("uuid", "6de16d4a-e851-11e5-9ce9-5e5517507c66")
                    .addParameter("useruuid", "8fa7f75a-57bf-4c6f-8db7-7e16067c1bcd")
                    .addParameter("salary", 25000)
                    .addParameter("activefrom", "2015-02-01")
                    .executeUpdate();

            con.createQuery(sqlSalary)
                    .addParameter("uuid", "7948c5e8-162c-4053-b905-0f59a21d7741")
                    .addParameter("useruuid", "ade4859d-9c2f-4071-a492-d6fb8bf421ad")
                    .addParameter("salary", 10000)
                    .addParameter("activefrom", "2014-02-01")
                    .executeUpdate();

            con.createQuery(sqlSalary)
                    .addParameter("uuid", "7948c5e8-162c-4053-b905-0f59a21d7741")
                    .addParameter("useruuid", "ade4859d-9c2f-4071-a492-d6fb8bf421ad")
                    .addParameter("salary", 75000)
                    .addParameter("activefrom", "2015-01-01")
                    .executeUpdate();
            con.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        String sqlRoles = "INSERT INTO usermanager.roles " +
                "(uuid, useruuid, role) " +
                "VALUES (:uuid, :useruuid, :role);";

        try (Connection con = sql2o.open()) {
            con.createQuery(sqlRoles)
                    .addParameter("uuid", "2bb7f6cf-b18e-4615-87d8-8f581b00c8df")
                    .addParameter("useruuid", "7948c5e8-162c-4053-b905-0f59a21d7746")
                    .addParameter("role", "tm.admin")
                    .executeUpdate();

            con.createQuery(sqlRoles)
                    .addParameter("uuid", "9bc7302a-9a0b-11e6-9f33-a24fc0d9649c")
                    .addParameter("useruuid", "8fa7f75a-57bf-4c6f-8db7-7e16067c1bcd")
                    .addParameter("role", "tm.user")
                    .executeUpdate();

            con.createQuery(sqlRoles)
                    .addParameter("uuid", "9bc73566-9a0b-11e6-9f33-a24fc0d9649c")
                    .addParameter("useruuid", "ade4859d-9c2f-4071-a492-d6fb8bf421ad")
                    .addParameter("role", "tm.user")
                    .executeUpdate();
            con.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
