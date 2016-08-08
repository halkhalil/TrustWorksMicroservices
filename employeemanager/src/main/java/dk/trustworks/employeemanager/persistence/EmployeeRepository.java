package dk.trustworks.employeemanager.persistence;

import com.google.inject.Inject;
import dk.trustworks.employeemanager.dto.Employee;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by hans on 15/07/16.
 */
public class EmployeeRepository {

    private final Sql2o sql2o;

    public EmployeeRepository(DataSource ds) {
        sql2o = new Sql2o(ds);
    }

    /*
     private String uuid;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private double hours;
    private double salary;
    private String email;
    private DateTime created;
     */

    public List<Employee> findAll() {
        System.out.println("findAll()");
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT uuid, username, firstname, lastname, hours, salary, email FROM employees u RIGHT JOIN ( " +
                    "select t.empuuid, t.status, t.validdate, t.hours, t.salary " +
                    "from contracts t " +
                    "inner join ( " +
                    "select empuuid, status, max(validdate) as MaxDate " +
                    "from contracts " +
                    "group by empuuid " +
                    ") " +
                    "tm on t.empuuid = tm.empuuid and t.validdate = tm.MaxDate " +
                    ") usi ON u.uuid = usi.empuuid;").executeAndFetch(Employee.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Employee find(String uuid) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT uuid, username, firstname, lastname, hours, salary, email FROM employees u RIGHT JOIN ( " +
                    "select t.empuuid, t.status, t.validdate, t.hours, t.salary " +
                    "from contracts t " +
                    "inner join ( " +
                    "select empuuid, status, max(validdate) as MaxDate " +
                    "from contracts " +
                    "group by empuuid " +
                    ") " +
                    "tm on t.empuuid = tm.empuuid and t.validdate = tm.MaxDate " +
                    ") usi ON u.uuid = usi.empuuid WHERE uuid LIKE :uuid;")
                    .addParameter("uuid", uuid)
                    .executeAndFetch(Employee.class).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
