package dk.trustworks.usermanager.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.usermanager.persistence.UserRepository;
import dk.trustworks.usermanager.service.UserService;
import io.undertow.server.HttpServerExchange;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 16/03/15.
 */
public class UserHandler extends DefaultHandler {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserHandler() {
        super("user");
        this.userService = new UserService();
        this.userRepository = new UserRepository();
        addCommand("capacitypermonth");
    }

    @Override
    protected DefaultLocalService getService() {
        return userService;
    }

    public void capacitypermonth(HttpServerExchange exchange, String[] params) {

        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());

        int capacityPerMonth[] = new int[12];

        for (int i = 0; i < 12; i++) {
            DateTime dateTime = new DateTime(year, i+1, 25, 0, 0);
            int capacityByMonth = userRepository.calculateCapacityByMonth(dateTime);
            capacityPerMonth[i] = capacityByMonth;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("capacitypermonth", capacityPerMonth);
        try {
            exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
