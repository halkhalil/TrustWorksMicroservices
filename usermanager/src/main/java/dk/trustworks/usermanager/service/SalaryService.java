package dk.trustworks.usermanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.usermanager.dto.Salary;
import dk.trustworks.usermanager.persistence.SalaryRepository;
import org.joda.time.DateTime;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 17/03/15.
 */
public class SalaryService {

    private SalaryRepository salaryRepository;

    public SalaryService(DataSource ds) {
        salaryRepository = new SalaryRepository(ds);
    }

    public List<Salary> findActiveByDate(DateTime date) {
        return salaryRepository.findActiveByDate(date);
    }

    public Map<String, double[]> usersalarypermonthbyyear(int year) {
        Map<String, double[]> result = new HashMap<>();
        for (int i = 0; i < 12; i++) {
            DateTime date = new DateTime(year, i+1, 1, 0, 0);
            List<Salary> activeUsers = salaryRepository.findActiveByDate(date);
            for (Salary activeUser : activeUsers) {
                result.putIfAbsent(activeUser.getUseruuid(), new double[12]);
                result.get(activeUser.getUseruuid())[i] = Double.parseDouble(activeUser.getSalary().toString());
            }
        }

        return result;
        /*
        try {
            exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        */
    }

    public void create(JsonNode jsonNode) throws SQLException {
        salaryRepository.create(jsonNode);
    }

    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        salaryRepository.update(jsonNode, uuid);
    }
}
