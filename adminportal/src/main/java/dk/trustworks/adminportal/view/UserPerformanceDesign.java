package dk.trustworks.adminportal.view;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Property;
import com.vaadin.ui.*;
import com.vaadin.ui.declarative.Design;
import dk.trustworks.adminportal.domain.AmountPerItem;
import dk.trustworks.adminportal.domain.Capacity;
import dk.trustworks.adminportal.domain.DataAccess;
import dk.trustworks.framework.model.User;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.Calendar;

@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class UserPerformanceDesign extends CssLayout {
    private final DataAccess dataAccess = new DataAccess();
    protected CssLayout dashboard_item5;
	protected CssLayout dashboard_item26;
	protected CssLayout dashboard_item27;
    protected CssLayout dashboard_item28;
    protected CssLayout dashboard_item29;
    protected HorizontalLayout sparkline_horizontal;

	public UserPerformanceDesign() {
        Design.read(this);
        Double[] revenuePerDay = dataAccess.getRevenuePerDay();
        final int[] intArray = new int[revenuePerDay.length];
        for (int i=0; i<intArray.length; ++i)
            intArray[i] = revenuePerDay[i].intValue();

        List<User> users = dataAccess.getUsers();

        NativeSelect year_select;
        year_select = new NativeSelect("Select year");
        for (int i = 2014; i < Calendar.getInstance().get(Calendar.YEAR); i++) {
            year_select.addItem(""+i);
        }

        String currentYear = ""+Calendar.getInstance().get(Calendar.YEAR);
        year_select.addItem(currentYear);
        year_select.setValue(currentYear);

        ListSelect userSelect = new ListSelect("Select an option");
        for (User user : users) {
            userSelect.addItem(user);
            userSelect.setItemCaption(user, user.username);
        }

        userSelect.setRows(6);
        userSelect.setNullSelectionAllowed(true);
        userSelect.setImmediate(true);
        userSelect.setMultiSelect(true);

        userSelect.addValueChangeListener(e -> {
            Notification.show("Users changed",
                    Notification.Type.TRAY_NOTIFICATION);
            createGraphs(Integer.parseInt((String)year_select.getValue()), (Set<User>) userSelect.getValue());
        });


        year_select.addValueChangeListener((Property.ValueChangeListener) e -> {
            Notification.show("Selected: ",
                    String.valueOf(e.getProperty().getValue()),
                    Notification.Type.TRAY_NOTIFICATION);
            createGraphs(Integer.parseInt((String)year_select.getValue()), (Set<User>) userSelect.getValue());
        });
        sparkline_horizontal.addComponent(year_select);
        sparkline_horizontal.addComponent(userSelect);
    }

    private void createGraphs(int year, Set<User> users) {
        RevenuePerMonthChart revenuePerMonthChart = new RevenuePerMonthChart(year, users);
        dashboard_item5.removeAllComponents();
        dashboard_item5.addComponent(revenuePerMonthChart);

        BillableHoursPerEmployeesChart billableHoursPerEmployeesChart = new BillableHoursPerEmployeesChart(year, users);
        dashboard_item26.removeAllComponents();
        dashboard_item26.addComponent(billableHoursPerEmployeesChart);

        VacationChart vacationSickChart = new VacationChart(year, users);
        dashboard_item27.removeAllComponents();
        dashboard_item27.addComponent(vacationSickChart);

        SickChart sickChart = new SickChart(year, users);
        dashboard_item28.removeAllComponents();
        dashboard_item28.addComponent(sickChart);

        dashboard_item29.removeAllComponents();
    }

    public class VacationChart extends Chart {

        public VacationChart(int year, Set<User> users) {
            setWidth("100%");
            setHeight("280px");

            setCaption("Vacation Days");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setType(ChartType.COLUMN);
            getConfiguration().getChart().setAnimation(true);
            getConfiguration().getxAxis().getLabels().setEnabled(true);

            getConfiguration().getxAxis().setTickWidth(0);
            getConfiguration().getyAxis().setTitle("");
            getConfiguration().getLegend().setEnabled((users.size() > 1));

            for (User user : users) {
                Double[] daysPerMonth = dataAccess.getFreeDaysPerMonthPerUser(year, user.getUseruuid());
                DataSeries sickdaysList = new DataSeries(user.username);

                String[] categories = new String[daysPerMonth.length];

                for (int i = 0; i < daysPerMonth.length; i++) {
                    sickdaysList.add(new DataSeriesItem(Month.of(i+1).getDisplayName(TextStyle.FULL, Locale.ENGLISH), daysPerMonth[i]));
                    categories[i] = Month.of(i+1).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                }

                getConfiguration().getxAxis().setCategories(categories);
                getConfiguration().addSeries(sickdaysList);
            }

            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }

    public class SickChart extends Chart {

        public SickChart(int year, Set<User> users) {
            setWidth("100%");
            setHeight("280px");

            setCaption("Sick Days");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setType(ChartType.COLUMN);
            getConfiguration().getChart().setAnimation(true);
            getConfiguration().getxAxis().getLabels().setEnabled(true);

            getConfiguration().getxAxis().setTickWidth(0);
            getConfiguration().getyAxis().setTitle("");
            getConfiguration().getLegend().setEnabled((users.size() > 1));

            for (User user : users) {
                Double[] daysPerMonth = dataAccess.getSickDaysPerMonthPerUser(year, user.getUseruuid());
                DataSeries sickdaysList = new DataSeries(user.username);

                String[] categories = new String[daysPerMonth.length];

                for (int i = 0; i < daysPerMonth.length; i++) {
                    sickdaysList.add(new DataSeriesItem(Month.of(i+1).getDisplayName(TextStyle.FULL, Locale.ENGLISH), daysPerMonth[i]));
                    categories[i] = Month.of(i+1).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                }

                getConfiguration().getxAxis().setCategories(categories);
                getConfiguration().addSeries(sickdaysList);
            }

            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }

    public class TopGrossingProjectsChart extends Chart {

        public TopGrossingProjectsChart(int year) {
            LocalDate periodStart = new LocalDate(year-1, 7, 01);
            LocalDate periodEnd = new LocalDate(year, 6, 30);

            setWidth("100%");  // 100% by default
            setHeight("280px"); // 400px by default
            //setSizeFull();

            setCaption("Top Grossing Projects");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setType(ChartType.COLUMN);
            getConfiguration().getChart().setAnimation(true);
            getConfiguration().getxAxis().getLabels().setEnabled(true);
            getConfiguration().getxAxis().setTickWidth(0);
            getConfiguration().getyAxis().setTitle("");
            getConfiguration().getLegend().setEnabled(false);

            List<AmountPerItem> amountPerItemList = dataAccess.getProjectRevenue(periodStart, periodEnd);
            Collections.sort(amountPerItemList);
            String[] categories = new String[amountPerItemList.size()];
            DataSeries listSeries = new DataSeries("Revenue");
            int i = 0;
            double sumOfRemainingProjects = 0.0;
            for (AmountPerItem amountPerItem : amountPerItemList) {
                if(i<10) {
                    listSeries.add(new DataSeriesItem(amountPerItem.description, amountPerItem.amount));
                    StringBuilder shortname = new StringBuilder();
                    String[] s = amountPerItem.description.split(" ");
                    //for (String s : amountPerItem.description.split(" ")) {
                    int subLength = s[0].length()<3?s[0].length():3;
                        shortname.append(s[0].substring(0,subLength));
                    //}
                    categories[i] = shortname.toString();
                } else {
                    sumOfRemainingProjects += amountPerItem.amount;
                    categories[10] = "Rest";
                }

                i++;
            }
            listSeries.add(new DataSeriesItem("Remaining projects", sumOfRemainingProjects));
            getConfiguration().getxAxis().setCategories(categories);
            getConfiguration().addSeries(listSeries);
            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }

    public class RevenuePerMonthChart extends Chart {

        public RevenuePerMonthChart(int year, Set<User> users) {
            LocalDate periodStart = new LocalDate(year-1, 7, 01);
            LocalDate periodEnd = new LocalDate(year, 6, 30);

            setWidth("100%");
            setHeight("280px");

            setCaption("Revenue and Budget per month");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setAnimation(true);
            getConfiguration().getxAxis().setCategories(new DateFormatSymbols(Locale.ENGLISH).getShortMonths());
            getConfiguration().getxAxis().setTickWidth(0);
            getConfiguration().getyAxis().setTitle("");
            getConfiguration().getLegend().setEnabled(false);

            if(users.size() == 1) {
                getConfiguration().getChart().setType(ChartType.AREASPLINE);
                User user = (User) users.toArray()[0];
                long[] revenuePerMonth = dataAccess.getRevenuePerMonthPerUser(periodStart, periodEnd, user.getUseruuid());

                double sumRevenue = 0.0;
                for (Long amountPerItem : revenuePerMonth) {
                    sumRevenue += amountPerItem;
                }
                double months = new DateTime().getMonthOfYear();
                if(year != new DateTime().getYear()) months = 12;
                double avgRevenue = sumRevenue / months;

                DataSeries avgRevenueList = new DataSeries("Average Revenue");
                PlotOptionsLine options2 = new PlotOptionsLine();
                options2.setColor(SolidColor.BLACK);
                options2.setMarker(new Marker(false));
                avgRevenueList.setPlotOptions(options2);

                DataSeries revenueSeries = new DataSeries("Revenue");
                for (int i = 0; i < 12; i++) {
                    revenueSeries.add(new DataSeriesItem(Month.of(i+1).getDisplayName(TextStyle.FULL, Locale.ENGLISH), revenuePerMonth[i]));
                    avgRevenueList.add(new DataSeriesItem("Average revenue", avgRevenue));
                }

                Long[] budgetPerMonth = dataAccess.getBudgetPerMonthByUser(year, user.UUID);
                DataSeries budgetSeries = new DataSeries("Budget");
                for (int i = 0; i < 12; i++) {
                    budgetSeries.add(new DataSeriesItem(Month.of(i+1).getDisplayName(TextStyle.FULL, Locale.ENGLISH), budgetPerMonth[i]));
                }

                getConfiguration().addSeries(revenueSeries);
                getConfiguration().addSeries(budgetSeries);
                getConfiguration().addSeries(avgRevenueList);
            } else if (users.size() > 1) {
                getConfiguration().getChart().setType(ChartType.COLUMN);
                getConfiguration().getLegend().setEnabled(true);
                for (User user : users) {
                    long[] revenuePerMonth = dataAccess.getRevenuePerMonthPerUser(periodStart, periodEnd, user.getUseruuid());

                    DataSeries revenueSeries = new DataSeries(user.username);
                    for (int i = 0; i < 12; i++) {
                        revenueSeries.add(new DataSeriesItem(Month.of(i+1).getDisplayName(TextStyle.FULL, Locale.ENGLISH), revenuePerMonth[i]));
                    }
                    getConfiguration().addSeries(revenueSeries);
                }

            }


            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }

    public class RevenuePerMonthByCapacityChart extends Chart {

        public RevenuePerMonthByCapacityChart(int year) {
            LocalDate periodStart = LocalDate.parse(year+"-01-01");
            LocalDate periodEnd = LocalDate.parse(year+"-12-31");

            setWidth("100%");  // 100% by default
            setHeight("280px"); // 400px by default
            //setSizeFull();

            setCaption("Revenue per month by Capacity");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setType(ChartType.SPLINE);
            getConfiguration().getChart().setAnimation(true);
            //getConfiguration().getxAxis().getLabels().setEnabled(false);
            getConfiguration().getxAxis().setCategories(new DateFormatSymbols(Locale.ENGLISH).getShortMonths());
            getConfiguration().getxAxis().setTickWidth(0);
            getConfiguration().getyAxis().setTitle("Revenue");
            getConfiguration().getLegend().setEnabled(false);

            Long[] revenuePerMonth = dataAccess.getRevenuePerMonthByCapacity(year);
            double sumRevenue = 0.0;
            for (Long amountPerItem : revenuePerMonth) {
                sumRevenue += amountPerItem;
            }
            double avgRevenue = sumRevenue / revenuePerMonth.length;
            DataSeries avgRevenueList = new DataSeries("Average Revenue");
            PlotOptionsLine options2 = new PlotOptionsLine();
            options2.setColor(SolidColor.BLACK);
            options2.setMarker(new Marker(false));
            avgRevenueList.setPlotOptions(options2);

            DataSeries revenueSeries = new DataSeries("Revenue");
            for (int i = 0; i < 12; i++) {
                revenueSeries.add(new DataSeriesItem(Month.of(i+1).getDisplayName(TextStyle.FULL, Locale.ENGLISH), revenuePerMonth[i]));
                avgRevenueList.add(new DataSeriesItem("Average revenue", avgRevenue));
            }

            //int[] capacityPerMonthByYear = dataAccess.getCapacityPerMonth(year);
            List<Capacity> capacityPerMonthByYearList = dataAccess.getCapacityPerMonth(periodStart, periodEnd);
            int[] capacityPerMonthByYear = new int[capacityPerMonthByYearList.size()];
            int j = 0;
            for (Capacity capacity : capacityPerMonthByYearList) {
                capacityPerMonthByYear[j++] = capacity.capacity;
            }
            ListSeries series2 = new ListSeries("Capacity");

            YAxis yaxis = new YAxis();
            yaxis.setTitle("Capacity");
            yaxis.setOpposite(true);
            yaxis.setMin(0);
            getConfiguration().addyAxis(yaxis);

            PlotOptionsLine options3 = new PlotOptionsLine();
            options3.setColor(SolidColor.RED);
            series2.setPlotOptions(options3);

            for (int i = 0; i < 12; i++) {
                series2.addData(capacityPerMonthByYear[i]/37.0f);
            }

            getConfiguration().addSeries(revenueSeries);
            getConfiguration().addSeries(series2);
            getConfiguration().addSeries(avgRevenueList);
            series2.setyAxis(yaxis);
            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }

    public class BillableHoursPerEmployeesChart extends Chart {

        public BillableHoursPerEmployeesChart(int year, Set<User> users) {
            setWidth("100%");
            setHeight("280px");

            setCaption("Billable Hours per Weekday");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setType(ChartType.SPLINE);
            getConfiguration().getChart().setAnimation(true);
            getConfiguration().getxAxis().getLabels().setEnabled(true);

            getConfiguration().getxAxis().setTickWidth(0);
            getConfiguration().getyAxis().setTitle("");
            getConfiguration().getLegend().setEnabled((users.size() > 1));

            for (User user : users) {
                double[] amountPerItemList = dataAccess.getBillableHoursPerUserPerDay(year, user.getUUID());

                DataSeries revenueList = new DataSeries(user.username);

                String[] categories = new String[amountPerItemList.length];
                int i = 0;
                for (double amountPerItem : amountPerItemList) {
                    revenueList.add(new DataSeriesItem(DayOfWeek.of(i+1).getDisplayName(TextStyle.FULL, Locale.ENGLISH), amountPerItem));
                    categories[i] = DayOfWeek.of(i+1).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                    i++;
                }
                getConfiguration().getxAxis().setCategories(categories);
                getConfiguration().addSeries(revenueList);
            }

            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }
}
