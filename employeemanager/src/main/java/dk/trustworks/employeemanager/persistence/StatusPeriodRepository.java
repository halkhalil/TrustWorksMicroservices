package dk.trustworks.employeemanager.persistence;

import dk.trustworks.employeemanager.dto.Contract;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 15/07/16.
 */
public class StatusPeriodRepository {

    Logger log = LoggerFactory.getLogger("dk.trustworks.employeemanager.repository");

    private final Sql2o sql2o;

    public StatusPeriodRepository(DataSource ds) {
        sql2o = new Sql2o(ds);
    }

    public List<Contract> findAllWithHistory() {
        log.debug("findAllWithHistory");
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT uuid, empuuid, hours, salary, status, validdate " +
                    "FROM contracts " +
                    "ORDER BY empuuid, validdate ASC;")
                    .executeAndFetch(Contract.class);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return new ArrayList<>();
    }

    public List<Contract> findAllWithHistoryDuringPeriod(LocalDate fromDate, LocalDate toDate) {
        log.debug("findAllWithHistoryDuringPeriod");
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT uuid, empuuid, hours, salary, status, validdate " +
                    "FROM contracts " +
                    "WHERE validdate >= :startDate AND validdate <= :endDate "+
                    "ORDER BY empuuid, validdate ASC;")
                    .addParameter("startDate", fromDate.toDate())
                    .addParameter("endDate", toDate.toDate())
                    .executeAndFetch(Contract.class);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return new ArrayList<>();
    }

    public List<Contract> findAllByDate(LocalDate date) {
        log.debug("findAllByDate");
        log.debug("date: "+date);
        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery("select t.uuid, t.empuuid, t.hours, t.salary, t.status, t.validdate " +
                    "from contracts t " +
                    "inner join ( " +
                    "select uuid, empuuid, hours, status, salary, max(validdate) as MaxDate " +
                    "from contracts " +
                    "WHERE validdate <= :date " +
                    "group by empuuid ) " +
                    "tm on t.empuuid = tm.empuuid and t.validdate = tm.MaxDate;")
                    .addParameter("date", date.toDate())
                    .executeAndFetch(Contract.class);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return new ArrayList<>();
    }
}
