package dk.trustworks.bimanager.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 29/12/15.
 */
public class Statistic {

    private final List<Map<String, Object>> root;

    private final Map<String, Object> finances;

    public Statistic() {
        this.root = new ArrayList<>();
        this.finances = new HashMap<>();
        root.add(finances);
    }

    public void addRevenuePerDayPast30Days(double[] revenuePerDay) {
        finances.put("revenuePerDayPast30Days", revenuePerDay);
    }

    public List<Map<String, Object>> getStatistic() {
        return root;
    }
}
