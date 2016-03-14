package dk.trustworks.usermanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.usermanager.persistence.SalaryRepository;
import dk.trustworks.usermanager.persistence.UserRepository;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.SQLException;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 17/03/15.
 */
public class SalaryService extends DefaultLocalService {

    private SalaryRepository salaryRepository;

    public SalaryService() {
        salaryRepository = new SalaryRepository();
    }

    public List<Map<String, Object>> findActiveByDate(Map<String, Deque<String>> queryParameters) {
        DateTime date = DateTime.parse(queryParameters.get("date").getFirst(), DateTimeFormat.forPattern("yyyy-MM-dd"));
        return salaryRepository.findActiveByDate(date);
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        salaryRepository.create(jsonNode);
    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        salaryRepository.update(jsonNode, uuid);
    }

    @Override
    public GenericRepository getGenericRepository() {
        return salaryRepository;
    }

    @Override
    public String getResourcePath() {
        return "salaries";
    }
}
