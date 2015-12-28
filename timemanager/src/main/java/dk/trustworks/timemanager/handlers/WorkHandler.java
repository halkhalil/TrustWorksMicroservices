package dk.trustworks.timemanager.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.timemanager.persistence.WorkRepository;
import dk.trustworks.timemanager.service.WorkService;
import io.undertow.server.HttpServerExchange;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 16/03/15.
 */
public class WorkHandler extends DefaultHandler {

    private final WorkService workService;
    private final WorkRepository workRepository;

    public WorkHandler() {
        super("work");
        this.workService = new WorkService();
        this.workRepository = new WorkRepository();
        addCommand("calculateworkduration");
    }

    public void calculateworkduration(HttpServerExchange exchange, String[] params) {
        System.out.println("WorkHandler.cloneweek");
        System.out.println("exchange = [" + exchange + "], params = [" + params + "]");
        double totalWorkDuration = workRepository.calculateTaskUserTotalDuration(exchange.getQueryParameters().get("taskuuid").getFirst(), exchange.getQueryParameters().get("useruuid").getFirst());
        Map<String, Object> result = new HashMap<>();
        result.put("totalworkduration", totalWorkDuration);
        try {
            exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected DefaultLocalService getService() {
        return workService;
    }
}
