package dk.trustworks.salesportal.view;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.Theme;
import com.vaadin.ui.Component;
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
public class SalesHeatMap {

    private LocalDate localDateStart;
    private LocalDate localDateEnd;

    private List<UserBudget> userBudgets;
    private Map<String, Double> userAvailability;

    private Sql2o sql2o;

    private int monthPeriod;

    private String[] monthNames;

    public SalesHeatMap(LocalDate localDateStart, LocalDate localDateEnd) {
        this.localDateStart = localDateStart;
        this.localDateEnd = localDateEnd;
        monthPeriod = new Period(localDateStart, localDateEnd, PeriodType.months()).getMonths();
        sql2o = new Sql2o(ConnectionHelper.getInstance().dataSource);
        System.out.println(1);
        getUserBudgets();
        System.out.println(2);
        getAmountPerItem();
        System.out.println(3);
        getMonthNames();
        System.out.println(4);
        System.out.println("localDateStart = " + localDateStart);
        System.out.println("localDateEnd = " + localDateEnd);
        System.out.println(5);
        System.out.println("monthPeriod = " + monthPeriod);
        System.out.println(6);
    }

    private void getMonthNames() {
        monthNames = new String[new Period(localDateStart, localDateEnd, PeriodType.months()).getMonths()];
        for (int i = 0; i < monthNames.length; i++) {
            monthNames[i] = localDateStart.plusMonths(i+1).monthOfYear().getAsShortText();
        }
    }

    private void getUserBudgets() {
        String sql = "SELECT u.uuid uuid, CONCAT(u.firstname, ' ', u.lastname) name, (((b.year*10000)+((b.month+1)*100))+1) date, SUM(b.budget / twc.price) budget " +
                "FROM clientmanager.taskworkerconstraint_latest b " +
                "INNER JOIN clientmanager.taskworkerconstraint twc ON twc.uuid = b.taskworkerconstraintuuid " +
                "INNER JOIN usermanager.user u ON u.uuid = twc.useruuid " +
                "WHERE ((b.year*10000)+((b.month+1)*100)) between :periodStart and :periodEnd " +
                "GROUP BY u.uuid, b.year, b.month " +
                "ORDER BY u.lastname DESC, uuid, date;";
        try(Connection con = sql2o.open()) {
            userBudgets = con.createQuery(sql)
                    .addParameter("periodStart", localDateStart.toString("yyyyMMdd"))
                    .addParameter("periodEnd", localDateEnd.toString("yyyyMMdd"))
                    .executeAndFetch(UserBudget.class);
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getCapacityByMonth(LocalDate localDate) {
        String sql = "SELECT sum(allocation) allocation FROM usermanager.user u RIGHT JOIN ( " +
                "select t.useruuid, t.status, t.statusdate, t.allocation " +
                "from usermanager.userstatus t " +
                "inner join ( " +
                "select useruuid, status, max(statusdate) as MaxDate " +
                "from usermanager.userstatus  WHERE statusdate < :monthdate " +
                "group by useruuid \n" +
                ") \n" +
                "tm on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate " +
                ") usi ON u.uuid = usi.useruuid;";
        try(Connection con = sql2o.open()) {
            Integer capacity = con.createQuery(sql)
                    .addParameter("monthdate", localDate.toString("yyyy-MM-dd"))
                    .executeScalar(Integer.class);
            con.close();
            return capacity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void getAmountPerItem() {
        String sql = "SELECT uuid, SUM(amount) amount, description FROM ( ";
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
        userAvailability = new HashMap<>();
        for (AmountPerItem amountPerItem : amountPerItemList) {
            System.out.println("amountPerItem = " + amountPerItem);
            userAvailability.put(amountPerItem.uuid, (amountPerItem.amount / monthPeriod) / 5.0);
            System.out.println("userAvailability = " + userAvailability.get(amountPerItem.uuid));
        }
    }

    public Component getChart() {
        System.out.println("userBudgets = " + userBudgets.size());
        System.out.println("userAvailability = " + userAvailability.size());

        Chart chart = new Chart();

        Configuration config = chart.getConfiguration();
        config.getChart().setType(ChartType.HEATMAP);
        config.getChart().setMarginTop(40);
        config.getChart().setMarginBottom(40);

        config.getTitle().setText("Employee Availability Per Month");

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

        HeatSeries rs = new HeatSeries("% availability");
        List<String> usersList = new ArrayList<>();
        int month = 0;
        int userNumber = -1;
        String useruuid = "";
        for (UserBudget userBudget : userBudgets) {
            if(!useruuid.equals(userBudget.uuid)) {
                if(userNumber>=0) {
                    month++;
                    for (int j = month; j < monthPeriod - 2; j++) {
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
            //System.out.println("userBudget.budget = " + userBudget.budget);
            //System.out.println("userAvailability = " + userAvailability.get(useruuid));
            int weekDays = countWeekDays(localDateStart.plusMonths(month), localDateStart.plusMonths(month + 1));
            double budget = Math.round(weekDays * userAvailability.get(useruuid) - userBudget.budget);
            //System.out.println("budget = " + budget);
            if(budget<0) budget = 0;
            budget = Math.round(budget / Math.round(weekDays * userAvailability.get(useruuid)) * 100.0);
            //System.out.println("budget = " + budget);
            rs.addHeatPoint(month, userNumber, Math.round(budget));
        }
        month++;
        for (int j = month; j < monthPeriod-2; j++) {
            System.out.println("month = " + j);
            rs.addHeatPoint(j, userNumber, 100);
        }

        //config.getxAxis().setCategories(monthList.stream().toArray(size -> new String[size]));
        config.getxAxis().setCategories(monthNames);
        config.getyAxis().setCategories(usersList.stream().toArray(size -> new String[size]));

        PlotOptionsHeatmap plotOptionsHeatmap = new PlotOptionsHeatmap();
        plotOptionsHeatmap.setDataLabels(new DataLabels());
        plotOptionsHeatmap.getDataLabels().setEnabled(true);
        plotOptionsHeatmap.getStates().getHover().setFillColor(SolidColor.BLACK);

        SeriesTooltip tooltip = new SeriesTooltip();
        tooltip.setHeaderFormat("{series.name}<br/>");
        tooltip.setPointFormat("Amount: <b>{point.value}</b> ");
        plotOptionsHeatmap.setTooltip(tooltip);
        config.setPlotOptions(plotOptionsHeatmap);

        config.setSeries(rs);

        chart.drawChart(config);

        return chart;
    }

    public Component getAvailabilityChart() {
        Chart chart = new Chart(ChartType.AREASPLINE);
        chart.setHeight("450px");

        Configuration conf = chart.getConfiguration();

        conf.setTitle(new Title("Total % availability"));

        Legend legend = new Legend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setAlign(HorizontalAlign.LEFT);
        legend.setFloating(true);
        legend.setVerticalAlign(VerticalAlign.TOP);
        legend.setX(150);
        legend.setY(100);
        conf.setLegend(legend);

        XAxis xAxis = new XAxis();
        xAxis.setCategories(monthNames);
        xAxis.setLineColor(SolidColor.GREEN);
        // add blue background for the weekend
        //PlotBand plotBand = new PlotBand(4.5, 6.5, SolidColor.GREEN);
        //plotBand.setZIndex(1);
        //xAxis.setPlotBands(plotBand);
        conf.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setTitle(new AxisTitle("Total Availability"));
        conf.addyAxis(yAxis);

        Tooltip tooltip = new Tooltip();
        // Customize tooltip formatting
        tooltip.setHeaderFormat("");
        tooltip.setPointFormat("{series.name}: {point.y} %");
        conf.setTooltip(tooltip);

        PlotOptionsAreaspline plotOptions = new PlotOptionsAreaspline();
        plotOptions.setColor(SolidColor.GREEN);
        //plotOptions.setNegativeColor(SolidColor.RED);
        //plotOptions.setNegativeFillColor(SolidColor.RED);
        //plotOptions.setThreshold(25);
        plotOptions.setFillOpacity(0.5);
        conf.setPlotOptions(plotOptions);

        List<String> usersList = new ArrayList<>();
        int month = 0;
        int userNumber = -1;
        String useruuid = "";


        int[] numbers = new int[12];
        int[] capacity = new int[12];
        for (UserBudget userBudget : userBudgets) {
            if(!useruuid.equals(userBudget.uuid)) {
                if(userNumber>=0) {
                    month++;
                    for (int j = month; j < monthPeriod - 2; j++) {
                        numbers[j] = numbers[j] + 100;
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
            capacity[month] += weekDays * userAvailability.get(useruuid);
            double budget = Math.round(weekDays * userAvailability.get(useruuid) - userBudget.budget);
            if(budget<0) budget = 0;
            budget = Math.round(budget / Math.round(weekDays * userAvailability.get(useruuid)) * 100.0);
            numbers[month] = numbers[month] + (int)Math.round(budget);
        }
        month++;
        for (int j = month; j < monthPeriod-2; j++) {
            System.out.println("month = " + j);
            numbers[j] = numbers[j] + 100;
            //rs.addHeatPoint(j, userNumber, 100);
        }
        ListSeries listSeries = new ListSeries();
        for (int j = 0; j < monthPeriod-2; j++) {
            //if(capacity[month] <= 0) continue;
            listSeries.addData(numbers[j] / usersList.size());
            month++;
        }

        conf.addSeries(listSeries);

        chart.drawChart(conf);

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

}
