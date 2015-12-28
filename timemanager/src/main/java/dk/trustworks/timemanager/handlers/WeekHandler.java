package dk.trustworks.timemanager.handlers;

import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.timemanager.persistence.WeekRepository;
import dk.trustworks.timemanager.service.WeekService;
import io.undertow.server.HttpServerExchange;

import java.util.List;
import java.util.Map;

/**
 * Created by hans on 16/03/15.
 */
public class WeekHandler extends DefaultHandler {

    private final WeekService weekService;
    private final WeekRepository weekRepository;

    public WeekHandler() {
        super("week");
        this.weekService = new WeekService();
        this.weekRepository = new WeekRepository();
        this.addCommand("cloneweek");
    }

    public void cloneweek(HttpServerExchange exchange, String[] params) {
        System.out.println("WeekService.clone");
        System.out.println("exchange = [" + exchange + "], params = [" + params + "]");
        List<Map<String, Object>> weeks = weekRepository.findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(Integer.parseInt(exchange.getQueryParameters().get("weeknumber").getFirst()) - 1, Integer.parseInt(exchange.getQueryParameters().get("year").getFirst()), exchange.getQueryParameters().get("useruuid").getFirst());

    }

    @Override
    protected DefaultLocalService getService() {
        return weekService;
    }
}
