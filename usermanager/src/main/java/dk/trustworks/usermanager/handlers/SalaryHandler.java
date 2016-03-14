package dk.trustworks.usermanager.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.usermanager.persistence.SalaryRepository;
import dk.trustworks.usermanager.persistence.UserRepository;
import dk.trustworks.usermanager.service.SalaryService;
import dk.trustworks.usermanager.service.UserService;
import io.undertow.server.HttpServerExchange;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 16/03/15.
 */
public class SalaryHandler extends DefaultHandler {

    private final SalaryService salaryService;
    private final SalaryRepository salaryRepository;

    public SalaryHandler() {
        super("salary");
        this.salaryService = new SalaryService();
        this.salaryRepository = new SalaryRepository();
        addCommand("usersalarypermonthbyyear");
    }

    @Override
    protected DefaultLocalService getService() {
        return salaryService;
    }

    public void usersalarypermonthbyyear(HttpServerExchange exchange, String[] params) {
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());

        Map<String, double[]> result = new HashMap<>();
        for (int i = 0; i < 12; i++) {
            DateTime date = new DateTime(year, i+1, 1, 0, 0);
            List<Map<String, Object>> activeUsers = salaryRepository.findActiveByDate(date);
            for (Map<String, Object> activeUser : activeUsers) {
                if(result.get(activeUser.get("useruuid").toString()) == null) result.put(activeUser.get("useruuid").toString(), new double[12]);
                result.get(activeUser.get("useruuid").toString())[i] = Double.parseDouble(activeUser.get("salary").toString());
            }
        }

        try {
            exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
