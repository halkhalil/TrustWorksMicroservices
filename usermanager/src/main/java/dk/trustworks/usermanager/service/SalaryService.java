package dk.trustworks.usermanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.usermanager.dto.Salary;
import dk.trustworks.usermanager.persistence.SalaryRepository;
import org.joda.time.DateTime;
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

    public SalaryService(DataSource ds) {
        salaryRepository = new SalaryRepository(ds);
    }

    public List<Salary> findActiveByDate(LocalDate date) {
        return salaryRepository.findActiveByDate(date);
    }

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

    public void create(JsonNode jsonNode) throws SQLException {
        salaryRepository.create(jsonNode);
    }

    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        salaryRepository.update(jsonNode, uuid);
    }
}
