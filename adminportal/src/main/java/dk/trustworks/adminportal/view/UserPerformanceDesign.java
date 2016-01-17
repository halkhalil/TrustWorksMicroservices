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
import dk.trustworks.adminportal.domain.DataAccess;
import dk.trustworks.adminportal.domain.User;

import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.Calendar;

/** 
 * !! DO NOT EDIT THIS FILE !!
 * 
 * This class is generated by Vaadin Designer and will be overwritten.
 * 
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { … }
 */

@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class UserPerformanceDesign extends CssLayout {
    private final DataAccess dataAccess = new DataAccess();
    protected Label billing_header_label;
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
        createGraphs(Calendar.getInstance().get(Calendar.YEAR), users.get(0).getUuid());

        NativeSelect year_select;
        year_select = new NativeSelect("Select year");
        for (int i = 2014; i < Calendar.getInstance().get(Calendar.YEAR); i++) {
            year_select.addItem(""+i);
        }

        String currentYear = ""+Calendar.getInstance().get(Calendar.YEAR);
        year_select.addItem(currentYear);
        year_select.setValue(currentYear);

        NativeSelect user_select;
        user_select = new NativeSelect("Select user");
        for (User user : users) {
            user_select.addItem(user);
            user_select.setItemCaption(user, user.getUsername());
        }
        user_select.setValue(users.get(0));


        year_select.addValueChangeListener((Property.ValueChangeListener) e -> {
            Notification.show("Selected: ",
                    String.valueOf(e.getProperty().getValue()),
                    Notification.Type.TRAY_NOTIFICATION);
            //createGraphs(Integer.parseInt((String)e.getProperty().getValue()));
            createGraphs(Integer.parseInt((String)year_select.getValue()), ((User) user_select.getValue()).getUuid());
        });
        sparkline_horizontal.addComponent(year_select);

        user_select.addValueChangeListener((Property.ValueChangeListener) e -> {
            Notification.show("Selected: ",
                    String.valueOf(((User)e.getProperty().getValue()).getUsername()),
                    Notification.Type.TRAY_NOTIFICATION);
            createGraphs(Integer.parseInt((String)year_select.getValue()), ((User) user_select.getValue()).getUuid());
        });
        sparkline_horizontal.addComponent(user_select);
    }

    private void createGraphs(int year, String userUUID) {
        RevenuePerMonthChart revenuePerMonthChart = new RevenuePerMonthChart(year, userUUID);
        dashboard_item5.removeAllComponents();
        dashboard_item5.addComponent(revenuePerMonthChart);
        //dashboard_item5.addComponent(topGrossingEmployeesChart);

        BillableHoursPerEmployeesChart billableHoursPerEmployeesChart = new BillableHoursPerEmployeesChart(year, userUUID);
        dashboard_item26.removeAllComponents();
        dashboard_item26.addComponent(billableHoursPerEmployeesChart);
        //dashboard_item26.addComponent(topGrossingProjectsChart);

        //RevenuePerMonthChart revenuePerMonthChart = new RevenuePerMonthChart(year, userUUID);
        dashboard_item27.removeAllComponents();
        dashboard_item27.addComponent(revenuePerMonthChart);

        RevenuePerMonthByCapacityChart revenuePerMonthByCapacityChart = new RevenuePerMonthByCapacityChart(year);
        dashboard_item28.removeAllComponents();
        //dashboard_item28.addComponent(revenuePerMonthByCapacityChart);

        //BillableHoursPerEmployeesChart billableHoursPerEmployeesChart = new BillableHoursPerEmployeesChart(year, userUUID);
        dashboard_item29.removeAllComponents();
        //dashboard_item29.addComponent(billableHoursPerEmployeesChart);
    }

    public class TopGrossingEmployeesChart extends Chart {

        public TopGrossingEmployeesChart(int year) {
            setWidth("100%");
            setHeight("280px");

            setCaption("Top Grossing Employees");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setType(ChartType.COLUMN);
            getConfiguration().getChart().setAnimation(true);
            getConfiguration().getxAxis().getLabels().setEnabled(true);

            getConfiguration().getxAxis().setTickWidth(0);
            getConfiguration().getyAxis().setTitle("");
            getConfiguration().getLegend().setEnabled(false);

            List<AmountPerItem> amountPerItemList = dataAccess.getUserRevenue(year);
            double sumRevenue = 0.0;
            for (AmountPerItem amountPerItem : amountPerItemList) {
                sumRevenue += amountPerItem.amount;
            }
            double avgRevenue = sumRevenue / amountPerItemList.size();

            Collections.sort(amountPerItemList);
            String[] categories = new String[amountPerItemList.size()];
            DataSeries revenueList = new DataSeries("Revenue");
            DataSeries avgRevenueList = new DataSeries("Average Revenue");
            PlotOptionsLine options2 = new PlotOptionsLine();
            options2.setColor(SolidColor.BLACK);
            options2.setMarker(new Marker(false));
            avgRevenueList.setPlotOptions(options2);

            int i = 0;
            for (AmountPerItem amountPerItem : amountPerItemList) {
                revenueList.add(new DataSeriesItem(amountPerItem.description, amountPerItem.amount));
                avgRevenueList.add(new DataSeriesItem("Average revenue", avgRevenue));
                StringBuilder shortname = new StringBuilder();
                for (String s : amountPerItem.description.split(" ")) {
                    shortname.append(s.charAt(0));
                }
                categories[i++] = shortname.toString();
            }
            //revenueList.add(new DataSeriesItem("Remaining projects", sumOfRemainingProjects));
            getConfiguration().getxAxis().setCategories(categories);
            getConfiguration().addSeries(revenueList);
            getConfiguration().addSeries(avgRevenueList);
            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }

    public class TopGrossingProjectsChart extends Chart {

        public TopGrossingProjectsChart(int year) {
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

            List<AmountPerItem> amountPerItemList = dataAccess.getProjectRevenue(year);
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

        public RevenuePerMonthChart(int year, String userUUID) {
            setWidth("100%");  // 100% by default
            setHeight("280px"); // 400px by default
            //setSizeFull();

            setCaption("Revenue and Budget per month");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setType(ChartType.AREASPLINE);
            getConfiguration().getChart().setAnimation(true);
            //getConfiguration().getxAxis().getLabels().setEnabled(false);
            getConfiguration().getxAxis().setCategories(new DateFormatSymbols(Locale.ENGLISH).getShortMonths());
            getConfiguration().getxAxis().setTickWidth(0);
            getConfiguration().getyAxis().setTitle("");
            getConfiguration().getLegend().setEnabled(false);

            Long[] revenuePerMonth = dataAccess.getRevenuePerMonthPerUser(year, userUUID);
            DataSeries revenueSeries = new DataSeries("Revenue");
            for (int i = 0; i < 12; i++) {
                revenueSeries.add(new DataSeriesItem(Month.of(i+1).getDisplayName(TextStyle.FULL, Locale.ENGLISH), revenuePerMonth[i]));
            }

            Long[] budgetPerMonth = dataAccess.getBudgetPerMonthByUser(year, userUUID);
            DataSeries budgetSeries = new DataSeries("Budget");
            for (int i = 0; i < 12; i++) {
                budgetSeries.add(new DataSeriesItem(Month.of(i+1).getDisplayName(TextStyle.FULL, Locale.ENGLISH), budgetPerMonth[i]));
            }

            getConfiguration().addSeries(revenueSeries);
            getConfiguration().addSeries(budgetSeries);
            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }

    public class RevenuePerMonthByCapacityChart extends Chart {

        public RevenuePerMonthByCapacityChart(int year) {
            setWidth("100%");  // 100% by default
            setHeight("280px"); // 400px by default
            //setSizeFull();

            setCaption("Revenue per month by Capacity");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setType(ChartType.AREASPLINE);
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

            int[] capacityPerMonthByYear = dataAccess.getCapacityPerMonthByYear(year);
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

        public BillableHoursPerEmployeesChart(int year, String userUUID) {
            setWidth("100%");
            setHeight("280px");

            setCaption("Billable Hours per Weekday");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setType(ChartType.AREASPLINE);
            getConfiguration().getChart().setAnimation(true);
            getConfiguration().getxAxis().getLabels().setEnabled(true);

            getConfiguration().getxAxis().setTickWidth(0);
            getConfiguration().getyAxis().setTitle("");
            getConfiguration().getLegend().setEnabled(false);

            double[] amountPerItemList = dataAccess.getBillableHoursPerUserPerDay(year, userUUID);

            DataSeries revenueList = new DataSeries("Hours");

            String[] categories = new String[amountPerItemList.length];
            int i = 0;
            for (double amountPerItem : amountPerItemList) {
                revenueList.add(new DataSeriesItem(DayOfWeek.of(i+1).getDisplayName(TextStyle.FULL, Locale.ENGLISH), amountPerItem));
                categories[i] = DayOfWeek.of(i+1).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                i++;
            }
            getConfiguration().getxAxis().setCategories(categories);
            getConfiguration().addSeries(revenueList);
            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }
}
