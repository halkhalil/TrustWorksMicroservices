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

import java.util.*;
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
    private Map<String, Double> userAvailabilityMap;

    private Sql2o sql2o;

    private int monthPeriod;

    private String[] monthNames;
    private double[] monthAvailabilites = new double[12];
    private double[] monthTotalAvailabilites = new double[12];

    public SalesHeatMap(LocalDate localDateStart, LocalDate localDateEnd) {
        System.out.println("SalesHeatMap.SalesHeatMap");
        System.out.println("localDateStart = [" + localDateStart + "], localDateEnd = [" + localDateEnd + "]");
        this.localDateStart = localDateStart;
        this.localDateEnd = localDateEnd;
        monthPeriod = new Period(localDateStart, localDateEnd, PeriodType.months()).getMonths();
        sql2o = new Sql2o(ConnectionHelper.getInstance().dataSource);
        getUserBudgets();
        getAmountPerItem();
        getMonthNames();
    }

    private void getMonthNames() {
        monthNames = new String[new Period(localDateStart, localDateEnd, PeriodType.months()).getMonths()];
        for (int i = 0; i < monthNames.length; i++) {
            monthNames[i] = localDateStart.plusMonths(i).monthOfYear().getAsShortText();
        }
    }

    private void getUserBudgets() {
        System.out.println("SalesHeatMap.getUserBudgets");
        System.out.println("localDateStart = " + localDateStart.minusMonths(1).toString("yyyyMMdd"));
        System.out.println("localDateEnd = " + localDateEnd.minusMonths(1).toString("yyyyMMdd"));
        String sql = "SELECT u.uuid uuid, CONCAT(u.firstname, ' ', u.lastname) name, (((b.year*10000)+((b.month+1)*100))+1) date, SUM(b.budget / twc.price) budget " +
                "FROM clientmanager.taskworkerconstraint_latest b " +
                "INNER JOIN clientmanager.taskworkerconstraint twc ON twc.taskuuid = b.taskuuid and twc.useruuid = b.useruuid " +
                "INNER JOIN usermanager.user u ON u.uuid = twc.useruuid " +
                "WHERE ((b.year*10000)+((b.month+1)*100)) between :periodStart and :periodEnd " +
                "GROUP BY u.uuid, b.year, b.month " +
                "ORDER BY u.lastname DESC, uuid, date;";
        try(Connection con = sql2o.open()) {
            userBudgets = con.createQuery(sql)
                    .addParameter("periodStart", localDateStart.minusMonths(1).toString("yyyyMMdd"))
                    .addParameter("periodEnd", localDateEnd.minusMonths(1).toString("yyyyMMdd"))
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
        List<List<AmountPerItem>> amountPerItemListList = new ArrayList<>();

        try(Connection con = sql2o.open()) {
            LocalDate localDate = localDateStart;
            do {
                List<AmountPerItem> amountPerItemList = new ArrayList<>();
                String sql = "SELECT u.uuid uuid, CONCAT(u.firstname, ' ', u.lastname) description, SUM(allocation) amount, usi.status status FROM usermanager.user u LEFT JOIN ( " +
                        "SELECT t.useruuid, t.status, t.statusdate, t.allocation from usermanager.userstatus t inner join ( " +
                        "SELECT useruuid, status, max(statusdate) as MaxDate from usermanager.userstatus WHERE statusdate <= :date group by useruuid ) tm " +
                        "ON t.useruuid = tm.useruuid AND t.statusdate = tm.MaxDate ) usi " +
                        "ON u.uuid = usi.useruuid WHERE status NOT LIKE 'TERMINATED' GROUP BY uuid";
                amountPerItemList = con.createQuery(sql)
                        .addParameter("date", localDate.toString("yyyy-MM-dd"))
                        .executeAndFetch(AmountPerItem.class);
                amountPerItemListList.add(amountPerItemList);
                localDate = localDate.plusMonths(1);
            } while (!localDate.isAfter(localDateEnd));

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        userAvailabilityMap = new HashMap<>();
        int month = 0;
        for (List<AmountPerItem> amountPerItemList : amountPerItemListList) {
            for (AmountPerItem amountPerItem : amountPerItemList) {
                userAvailabilityMap.put(amountPerItem.uuid+month, (amountPerItem.amount / 5.0));
            }
            month++;
        }
    }

    public static void main(String[] args) {
        outerloop:
        for (int i=0; i < 5; i++) {
            for (int j=0; j < 5; j++) {
                if (i * j > 6) {
                    System.out.println("Breaking");
                    System.out.println(i + " " + j);
                    break;
                }
                System.out.println("is breaking");
                System.out.println(i + " " + j);
            }
        }
        System.out.println("Done");
    }

    public Component getChart() {
        System.out.println("userBudgets = " + userBudgets.size());
        System.out.println("userAvailabilityMap = " + userAvailabilityMap.size());

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
        //int month = 0;
        int userNumber = 0;
        //String useruuid = "";

        Map<String, List<UserBudget>> userMap = new HashMap<>();
        Map<String, Integer> userNumberMap = new HashMap<>();
        //List<String> userNameMap = new ArrayList<>();
        for (UserBudget userBudget : userBudgets) {
            userMap.putIfAbsent(userBudget.uuid, new ArrayList<>());
            if(!userNumberMap.containsKey(userBudget.uuid)) {
                userNumberMap.put(userBudget.uuid, userNumber++);
                usersList.add(userBudget.name);
            }
            //System.out.println("userNumber = " + userNumber);

        }
        //usersList.addAll(userNameMap);
        for (String s : usersList) {
            System.out.println("usernames = " + s);
        }


        for (String userUUID : userMap.keySet()) {
            for (int month = 0; month < 12; month++) {
                String currentDate = localDateStart.plusMonths(month).toString("yyyyMMdd");
                boolean foundBudget = false;
                for (UserBudget userBudget : userBudgets) {
                    if(userBudget.uuid.equals(userUUID)) {
                        //if(userBudget.uuid.equals("ade4859d-9c2f-4071-a492-d6fb8bf421ad")) System.out.println("userBudget = " + userBudget);
                        if(userBudget.date.equals(currentDate)) {
                            if(userBudget.uuid.equals("ade4859d-9c2f-4071-a492-d6fb8bf421ad")) System.out.println("userBudget = " + userBudget);
                            //if(userBudget.uuid.equals("ade4859d-9c2f-4071-a492-d6fb8bf421ad")) System.out.println("found date = " + currentDate);
                            int weekDays = countWeekDays(localDateStart.plusMonths(month), localDateStart.plusMonths(month + 1));
                            Double userAvailability = userAvailabilityMap.get(userUUID + month);
                            if(userAvailability == null) userAvailability = 0.0;
                            double budget = Math.round((weekDays * userAvailability) - userBudget.budget);
                            if(budget<0) budget = 0;
                            budget = Math.round(budget / Math.round(weekDays * userAvailability) * 100.0);
                            if(userBudget.uuid.equals("ade4859d-9c2f-4071-a492-d6fb8bf421ad")) System.out.println(month + ", " + userNumberMap.get(userUUID) + ", " + Math.round(budget));
                            rs.addHeatPoint(month, userNumberMap.get(userUUID), Math.round(budget));
                            monthAvailabilites[month] += Math.round(budget);
                            monthTotalAvailabilites[month] += 100;
                            foundBudget = true;
                            break;
                        }
                    }
                }
                if (!foundBudget) {
                    // Didn't find budget for user and month
                    if (userUUID.equals("ade4859d-9c2f-4071-a492-d6fb8bf421ad"))
                        System.out.println("Didn't find date: " + month);
                    if(userUUID.equals("ade4859d-9c2f-4071-a492-d6fb8bf421ad")) System.out.println(month + ", " + userNumberMap.get(userUUID) + ", " + 100);
                    rs.addHeatPoint(month, userNumberMap.get(userUUID), 100);
                    monthAvailabilites[month] += Math.round(100);
                    monthTotalAvailabilites[month] += 100;
                }
            }
        }

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
        plotOptions.setFillOpacity(0.5);
        conf.setPlotOptions(plotOptions);

        ListSeries listSeries = new ListSeries();
        for (int j = 0; j < monthPeriod; j++) {
            System.out.println("monthAvailabilites[j] = " + monthAvailabilites[j]);
            System.out.println("monthTotalAvailabilites[j] = " + monthTotalAvailabilites[j]);
            listSeries.addData(Math.round(monthAvailabilites[j] / monthTotalAvailabilites[j] * 100.0));
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
