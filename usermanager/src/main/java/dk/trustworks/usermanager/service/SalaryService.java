package dk.trustworks.usermanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.security.Authenticator;
import dk.trustworks.framework.security.RoleRight;
import dk.trustworks.usermanager.dto.Salary;
import dk.trustworks.usermanager.persistence.SalaryRepository;
import net.sf.cglib.proxy.Enhancer;
import org.joda.time.LocalDate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 17/03/15.
 */
public class SalaryService {

    private SalaryRepository salaryRepository;

    public SalaryService() {
    }

    public SalaryService(DataSource ds) {
        salaryRepository = new SalaryRepository(ds);
    }

    public static SalaryService getInstance(DataSource ds) {
        SalaryService service = new SalaryService(ds);
        return (SalaryService) Enhancer.create(service.getClass(), new Authenticator(service));
    }

    @RoleRight("tm.admin")
    public List<Salary> findActiveByDate(LocalDate date) {
        return salaryRepository.findActiveByDate(date);
    }

    @RoleRight("tm.admin")
    public List<Salary> usersalarypermonthbyyear(LocalDate periodStart, LocalDate periodEnd) {
        List<Salary> salaries = new ArrayList<>();

        LocalDate currentDate = periodStart;
        while(currentDate.isBefore(periodEnd)) {
            List<Salary> activeByDate = salaryRepository.findActiveByDate(currentDate);
            for (Salary salary : activeByDate) {
                salary.activeDate = currentDate;
            }
            salaries.addAll(activeByDate);
            currentDate = currentDate.plusMonths(1);
        }

        return salaries;
    }

    @RoleRight("tm.admin")
    public List<Salary> usersalarypermonthbyyearbyuser(String userUUID, LocalDate periodStart, LocalDate periodEnd) {
        List<Salary> salaries = new ArrayList<>();

        LocalDate currentDate = periodStart;
        while(currentDate.isBefore(periodEnd)) {
            List<Salary> activeByDate = salaryRepository.findActiveByDateAndUser(userUUID, currentDate);
            for (Salary salary : activeByDate) {
                salary.activeDate = currentDate;
            }
            salaries.addAll(activeByDate);
            currentDate = currentDate.plusMonths(1);
        }

        return salaries;
    }

    @RoleRight("tm.admin")
    public void create(JsonNode jsonNode) throws SQLException {
        salaryRepository.create(jsonNode);
    }

    @RoleRight("tm.admin")
    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        salaryRepository.update(jsonNode, uuid);
    }
}
