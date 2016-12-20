package dk.trustworks.salesportal.view;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.Theme;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.salesportal.db.ConnectionHelper;
import dk.trustworks.salesportal.model.AmountPerItem;
import dk.trustworks.salesportal.model.UserBudget;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by hans on 19/12/2016.
 */

@DesignRoot
@Theme("valo")
public class SalesHeatMap extends VerticalLayout {

    public Component getChart() {
        LocalDate localDateStart = LocalDate.now();
        LocalDate localDateEnd = LocalDate.now().plusYears(1);
        double monthPeriod = new Period(localDateStart, localDateEnd, PeriodType.months()).getMonths();

        Sql2o sql2o = new Sql2o(ConnectionHelper.getInstance().dataSource);

        String sql = "SELECT u.uuid uuid, CONCAT(u.firstname, ' ', u.lastname) name, (((b.year*10000)+((b.month+1)*100))+1) date, SUM(b.budget / twc.price) budget " +
                "FROM clientmanager.taskworkerconstraint_latest b " +
                "INNER JOIN clientmanager.taskworkerconstraint twc ON twc.uuid = b.taskworkerconstraintuuid " +
                "INNER JOIN usermanager.user u ON u.uuid = twc.useruuid " +
                "WHERE ((b.year*10000)+((b.month+1)*100)) between :periodStart and :periodEnd " +
                "GROUP BY u.uuid, b.year, b.month " +
                "ORDER BY u.lastname DESC, uuid, date;";
        List<UserBudget> userBudgets = new ArrayList<>();
        try(Connection con = sql2o.open()) {
            System.out.println(1);
            userBudgets = con.createQuery(sql)
                    .addParameter("periodStart", localDateStart.toString("yyyyMMdd"))
                    .addParameter("periodEnd", localDateEnd.toString("yyyyMMdd"))
                    .executeAndFetch(UserBudget.class);
            System.out.println(2);
            con.close();
            System.out.println(3);
        } catch (Exception e) {
            System.out.println(4);
            e.printStackTrace();
        }

        sql = "SELECT uuid, SUM(amount) amount, description FROM ( ";
        LocalDate localDate = localDateStart;
        do {
            sql += "SELECT u.uuid uuid, CONCAT(u.firstname, ' ', u.lastname) description, SUM(allocation) amount FROM usermanager.user u RIGHT JOIN ( " +
                    "SELECT t.useruuid, t.status, t.statusdate, t.allocation from usermanager.userstatus t inner join ( " +
                    "SELECT useruuid, status, max(statusdate) as MaxDate from usermanager.userstatus WHERE statusdate <= '"+localDate.toString("yyyy-MM-dd")+"' group by useruuid ) tm " +
                    "ON t.useruuid = tm.useruuid AND t.statusdate = tm.MaxDate ) usi " +
                    "ON u.uuid = usi.useruuid GROUP BY uuid";
            localDate = localDate.plusMonths(1);
            if(!localDate.isEqual(localDateEnd)) sql += " UNION ALL ";
        } while (!localDate.isEqual(localDateEnd));
        sql += ") m2 GROUP BY uuid;";

        List<AmountPerItem> amountPerItemList = new ArrayList<>();
        try(Connection con = sql2o.open()) {
            amountPerItemList = con.createQuery(sql).executeAndFetch(AmountPerItem.class);
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Double> userAvailability = new HashMap<>();
        for (AmountPerItem amountPerItem : amountPerItemList) {
            userAvailability.put(amountPerItem.uuid, (amountPerItem.amount / monthPeriod) / 5.0);
        }


        Chart chart = new Chart();

        Configuration config = chart.getConfiguration();
        config.getChart().setType(ChartType.HEATMAP);
        config.getChart().setMarginTop(40);
        config.getChart().setMarginBottom(40);

        config.getTitle().setText("Employee Availability Per Month");

        /*
        String[] users = userBudgets.stream().filter(distinctByKey(p -> p.name)).toArray(size -> new String[size]);

        String[] monthNames = new String[new Period(localDateStart, localDateEnd, PeriodType.months()).getMonths()];
        for (int i = 0; i < monthNames.length; i++) {
            monthNames[i] = localDateStart.plusMonths(i).monthOfYear().getAsShortText();
        }
        */

        config.getColorAxis().setMin(0);
        config.getColorAxis().setMax(100);
        config.getColorAxis().setMinColor(SolidColor.WHITE);
        config.getColorAxis().setMaxColor(SolidColor.GREEN);

        config.getLegend().setLayout(LayoutDirection.VERTICAL);
        config.getLegend().setAlign(HorizontalAlign.RIGHT);
        config.getLegend().setMargin(0);
        config.getLegend().setVerticalAlign(VerticalAlign.TOP);
        config.getLegend().setY(25);
        config.getLegend().setSymbolHeight(320);
        ((PlotOptionsHeatmap)config.getPlotOptions(ChartType.HEATMAP)).getStates().getHover().setFillColor(SolidColor.BLACK);

        HeatSeries rs = new HeatSeries("% availability");
        List<String> usersList = new ArrayList<>();
        List<String> monthList = new ArrayList<>();
        int month = 0;
        int userNumber = -1;
        String useruuid = "";

        for (UserBudget userBudget : userBudgets) {
            if(!useruuid.equals(userBudget.uuid)) {
                if(userNumber>=0) {
                    for (int j = ++month; j < monthPeriod - 2; j++) {
                        rs.addHeatPoint(j, userNumber, 100);
                    }
                }
                month = 0;
                usersList.add(userBudget.name);
                useruuid = userBudget.uuid;
                userNumber++;
            } else {
                month++;
            }

            int weekDays = countWeekDays(localDateStart.plusMonths(month), localDateStart.plusMonths(month + 1));
            double budget = Math.round(weekDays * userAvailability.get(useruuid) - userBudget.budget);
            if(budget<0) budget = 0;
            budget = Math.round(budget / Math.round(weekDays * userAvailability.get(useruuid)) * 100.0);

            monthList.add(localDateStart.plusMonths(month).monthOfYear().getAsShortText());
            //System.out.println("userBudget = " + userBudget);
            //System.out.println("month = " + month);
            //System.out.println("userNumber = " + userNumber);
            rs.addHeatPoint(month, userNumber, Math.round(budget));
        }
        for (int j = ++month; j < monthPeriod-2; j++) {
            rs.addHeatPoint(j, userNumber, 100);
        }

        System.out.println("usersList = " + usersList.size());

        config.getxAxis().setCategories(monthList.stream().toArray(size -> new String[size]));
        config.getyAxis().setCategories(usersList.stream().toArray(size -> new String[size]));

        PlotOptionsHeatmap plotOptionsHeatmap = new PlotOptionsHeatmap();
        plotOptionsHeatmap.setDataLabels(new DataLabels());
        plotOptionsHeatmap.getDataLabels().setEnabled(true);

        SeriesTooltip tooltip = new SeriesTooltip();
        tooltip.setHeaderFormat("{series.name}<br/>");
        tooltip.setPointFormat("Amount: <b>{point.value}</b> ");
        plotOptionsHeatmap.setTooltip(tooltip);
        config.setPlotOptions(plotOptionsHeatmap);

        config.setSeries(rs);

        chart.drawChart(config);

        return chart;
    }

    public int countWeekDays(LocalDate periodStart, LocalDate periodEnd) {
        LocalDate weekday = periodStart;

        if (periodStart.getDayOfWeek() == DateTimeConstants.SATURDAY ||
                periodStart.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            weekday = weekday.plusWeeks(1).withDayOfWeek(DateTimeConstants.MONDAY);
        }

        int count = 0;
        while (weekday.isBefore(periodEnd)) {
            count++;
            if (weekday.getDayOfWeek() == DateTimeConstants.FRIDAY)
                weekday = weekday.plusDays(3);
            else
                weekday = weekday.plusDays(1);
        }
        return count;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * Raw data to the heatmap chart
     *
     * @return Array of arrays of numbers.
     */
    private Number[][] getRawData() {
        return new Number[][] { { 0, 0, 0 }, { 0, 1, 19 }, { 0, 2, 8 },
                { 0, 3, 24 }, { 0, 4, 67 }, { 1, 0, 92 }, { 1, 1, 58 },
                { 1, 2, 78 }, { 1, 3, 117 }, { 1, 4, 48 }, { 2, 0, 35 },
                { 2, 1, 15 }, { 2, 2, 123 }, { 2, 3, 64 }, { 2, 4, 52 },
                { 3, 0, 72 }, { 3, 1, 132 }, { 3, 2, 114 }, { 3, 3, 19 },
                { 3, 4, 16 }, { 4, 0, 38 }, { 4, 1, 5 }, { 4, 2, 8 },
                { 4, 3, 117 }, { 4, 4, 115 }, { 5, 0, 88 }, { 5, 1, 32 },
                { 5, 2, 12 }, { 5, 3, 6 }, { 5, 4, 120 }, { 6, 0, 13 },
                { 6, 1, 44 }, { 6, 2, 88 }, { 6, 3, 98 }, { 6, 4, 96 },
                { 7, 0, 31 }, { 7, 1, 1 }, { 7, 2, 82 }, { 7, 3, 32 },
                { 7, 4, 30 }, { 8, 0, 85 }, { 8, 1, 97 }, { 8, 2, 123 },
                { 8, 3, 64 }, { 8, 4, 84 }, { 9, 0, 47 }, { 9, 1, 114 },
                { 9, 2, 31 }, { 9, 3, 48 }, { 9, 4, 91 } };
    }
}
