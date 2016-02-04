package dk.trustworks.adminportal.view;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.YAxis.Stop;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Property;
import com.vaadin.ui.*;
import com.vaadin.ui.declarative.Design;
import dk.trustworks.adminportal.component.SparklineChart;
import dk.trustworks.adminportal.domain.AmountPerItem;
import dk.trustworks.adminportal.domain.DataAccess;
import dk.trustworks.adminportal.domain.Expense;
import dk.trustworks.adminportal.domain.User;
import org.joda.time.DateTime;

import java.lang.reflect.Array;
import java.text.DateFormatSymbols;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.Calendar;
import java.util.List;

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
public class DashboardDesign extends CssLayout {
    private final DataAccess dataAccess = new DataAccess();

	protected Label billing_header_label;
	protected CssLayout dashboard_item5;
	protected CssLayout dashboard_item26;
	protected CssLayout dashboard_item27;
    protected CssLayout dashboard_item28;
    protected CssLayout dashboard_item29;
    protected CssLayout dashboard_item30;
    protected CssLayout dashboard_item31;
    protected CssLayout dashboard_item32;
    protected HorizontalLayout sparkline_horizontal;

    public static void main(String[] args) {
        int year = 2016;
        double weeks = 46.14285714285714;
        if(year == new DateTime().getYear()) weeks = (new DateTime().getDayOfYear() / 7.80769230769217f);
        System.out.println("new DateTime().getDayOfYear() = " + new DateTime().getDayOfYear());
        System.out.println("weeks = " + weeks);
        System.out.println(Math.round((215 / weeks) * 100.0) / 100.0);
    }

	public DashboardDesign() {
		Design.read(this);
        Double[] revenuePerDay = dataAccess.getRevenuePerDay();
        final int[] intArray = new int[revenuePerDay.length];
        for (int i=0; i<intArray.length; ++i)
            intArray[i] = revenuePerDay[i].intValue();

        createGraphs(Calendar.getInstance().get(Calendar.YEAR));

        sparkline_horizontal.addComponent(new SparklineChart("income per day", "kkr", "today ", new SolidColor("#AAAA00"), intArray));
        //sparkline_horizontal.addComponent(new SparklineChart("income per day 2", "kkr", "today ", new SolidColor("#AAAA00"), intArray));

        NativeSelect year_select;
        year_select = new NativeSelect("");
        for (int i = 2014; i < Calendar.getInstance().get(Calendar.YEAR); i++) {
            year_select.addItem(""+i);
        }

        String currentYear = ""+Calendar.getInstance().get(Calendar.YEAR);
        year_select.addItem(currentYear);
        year_select.setValue(currentYear);

        year_select.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent e) {
                Notification.show("Selected: ",
                        String.valueOf(e.getProperty().getValue()),
                        Notification.Type.TRAY_NOTIFICATION);
                createGraphs(Integer.parseInt((String)e.getProperty().getValue()));
            }
        });
        sparkline_horizontal.addComponent(year_select);
    }

    private void createGraphs(int year) {
        WorkRegistrationDelayChart workRegistrationDelayChart = new WorkRegistrationDelayChart(year);
        dashboard_item30.removeAllComponents();
        dashboard_item30.addComponent(workRegistrationDelayChart);

        TopGrossingEmployeesChart topGrossingEmployeesChart = new TopGrossingEmployeesChart(year);
        dashboard_item5.removeAllComponents();
        dashboard_item5.addComponent(topGrossingEmployeesChart);

        TopGrossingProjectsChart topGrossingProjectsChart = new TopGrossingProjectsChart(year);
        dashboard_item26.removeAllComponents();
        dashboard_item26.addComponent(topGrossingProjectsChart);

        RevenuePerMonthChart revenuePerMonthChart = new RevenuePerMonthChart(year);
        dashboard_item27.removeAllComponents();
        dashboard_item27.addComponent(revenuePerMonthChart);

        RevenuePerMonthByCapacityChart revenuePerMonthByCapacityChart = new RevenuePerMonthByCapacityChart(year);
        dashboard_item28.removeAllComponents();
        dashboard_item28.addComponent(revenuePerMonthByCapacityChart);

        BillableHoursPerEmployeesChart billableHoursPerEmployeesChart = new BillableHoursPerEmployeesChart(year);
        dashboard_item29.removeAllComponents();
        dashboard_item29.addComponent(billableHoursPerEmployeesChart);

        RevenueRateChart revenueRateChart = new RevenueRateChart(year);
        dashboard_item31.removeAllComponents();
        dashboard_item31.addComponent(revenueRateChart);

        ProjectDetailChart projectDetailChart = new ProjectDetailChart();
        dashboard_item32.removeAllComponents();
        //dashboard_item32.addComponent(projectDetailChart);
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

        public RevenuePerMonthChart(int year) {
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

            Long[] revenuePerMonth = dataAccess.getRevenuePerMonth(year);

            Long[] allExpenses = dataAccess.getExpensesByYear(year);

            DataSeries expensesList = new DataSeries("Expenses");
            PlotOptionsAreaSpline options3 = new PlotOptionsAreaSpline();
            options3.setColor(SolidColor.RED);
            options3.setMarker(new Marker(false));
            expensesList.setPlotOptions(options3);

            DataSeries revenueSeries = new DataSeries("Revenue");
            for (int i = 0; i < 12; i++) {
                revenueSeries.add(new DataSeriesItem(Month.of(i+1).getDisplayName(TextStyle.FULL, Locale.ENGLISH), revenuePerMonth[i]));
                expensesList.add(new DataSeriesItem("Expense for "+Month.of(i+1).getDisplayName(TextStyle.FULL, Locale.ENGLISH), allExpenses[i]));
            }

            Long[] budgetPerMonth = dataAccess.getBudgetPerMonth(year);
            DataSeries budgetSeries = new DataSeries("Budget");
            PlotOptionsAreaSpline options2 = new PlotOptionsAreaSpline();
            options2.setColor(SolidColor.ORANGE);
            //options2.setMarker(new Marker(false));
            budgetSeries.setPlotOptions(options2);
            for (int i = 0; i < 12; i++) {
                budgetSeries.add(new DataSeriesItem(Month.of(i+1).getDisplayName(TextStyle.FULL, Locale.ENGLISH), budgetPerMonth[i]));
            }

            getConfiguration().addSeries(budgetSeries);
            getConfiguration().addSeries(revenueSeries);
            getConfiguration().addSeries(expensesList);
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
            options2.setColor(SolidColor.DARKBLUE);
            options2.setMarker(new Marker(false));
            avgRevenueList.setPlotOptions(options2);

            Long[] allExpenses = dataAccess.getExpensesByCapacityByYear(year);

            DataSeries expensesList = new DataSeries("Expenses");
            PlotOptionsAreaSpline options3 = new PlotOptionsAreaSpline();
            options3.setColor(SolidColor.RED);
            options3.setMarker(new Marker(false));
            expensesList.setPlotOptions(options3);

            DataSeries revenueSeries = new DataSeries("Revenue");
            for (int i = 0; i < 12; i++) {
                revenueSeries.add(new DataSeriesItem(Month.of(i+1).getDisplayName(TextStyle.FULL, Locale.ENGLISH), revenuePerMonth[i]));
                avgRevenueList.add(new DataSeriesItem("Average revenue for "+Month.of(i+1).getDisplayName(TextStyle.FULL, Locale.ENGLISH), avgRevenue));
                expensesList.add(new DataSeriesItem("Expense for "+Month.of(i+1).getDisplayName(TextStyle.FULL, Locale.ENGLISH), allExpenses[i]));
            }

            int[] capacityPerMonthByYear = dataAccess.getCapacityPerMonthByYear(year);
            ListSeries capacityList = new ListSeries("Capacity");

            YAxis yaxis = new YAxis();
            yaxis.setTitle("Capacity");
            yaxis.setOpposite(true);
            yaxis.setMin(0);
            getConfiguration().addyAxis(yaxis);

            PlotOptionsLine options4 = new PlotOptionsLine();
            options4.setColor(SolidColor.GRAY);
            options4.setMarker(new Marker(false));
            capacityList.setPlotOptions(options4);

            for (int i = 0; i < 12; i++) {
                capacityList.addData(capacityPerMonthByYear[i]/37.0f);
            }

            getConfiguration().addSeries(revenueSeries);
            getConfiguration().addSeries(expensesList);
            getConfiguration().addSeries(capacityList);
            getConfiguration().addSeries(avgRevenueList);
            capacityList.setyAxis(yaxis);
            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }

    public class BillableHoursPerEmployeesChart extends Chart {

        public BillableHoursPerEmployeesChart(int year) {
            setWidth("100%");  // 100% by default
            setHeight("280px"); // 400px by default
            //setSizeFull();

            setCaption("Billable Hours per Employee");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setType(ChartType.COLUMN);
            getConfiguration().getChart().setAnimation(true);
            getConfiguration().getxAxis().getLabels().setEnabled(true);

            getConfiguration().getxAxis().setTickWidth(0);
            getConfiguration().getyAxis().setTitle("");
            getConfiguration().getLegend().setEnabled(false);

            List<AmountPerItem> amountPerItemList = dataAccess.getBillableHoursPerUser(year);

            Map<String, Integer> userVacation = new HashMap<>();
            for (User user : dataAccess.getUsers()) {
                int vacationDays = 0;
                for (Double days : dataAccess.getFreeDaysPerMonthPerUser(year, user.getUseruuid())) {
                    vacationDays += days;
                }
                userVacation.put(user.getUseruuid(), vacationDays);
            }

            double sumHours = 0.0;
            for (AmountPerItem amountPerItem : amountPerItemList) {
                sumHours += amountPerItem.amount;
            }
            double avgRevenue = sumHours / amountPerItemList.size();

            Collections.sort(amountPerItemList);
            String[] categories = new String[amountPerItemList.size()];
            DataSeries revenueList = new DataSeries("Hours");
            DataSeries avgRevenueList = new DataSeries("Average Hours");
            PlotOptionsLine options2 = new PlotOptionsLine();
            options2.setColor(SolidColor.BLACK);
            options2.setMarker(new Marker(false));
            avgRevenueList.setPlotOptions(options2);

            DataSeries series2 = new DataSeries("Average per week");

            YAxis yaxis = new YAxis();
            yaxis.setTitle("Avg per week");
            yaxis.setOpposite(true);
            yaxis.setMin(0);
            getConfiguration().addyAxis(yaxis);

            PlotOptionsLine options3 = new PlotOptionsLine();
            options3.setColor(SolidColor.RED);
            series2.setPlotOptions(options3);

            int i = 0;
            for (AmountPerItem amountPerItem : amountPerItemList) {
                revenueList.add(new DataSeriesItem(amountPerItem.description, amountPerItem.amount));
                double weeks = 52;
                if(year == new DateTime().getYear()) weeks = ((new DateTime().getDayOfYear() - userVacation.get(amountPerItem.uuid)) / 7.0);
                series2.add(new DataSeriesItem(amountPerItem.description, (Math.round(((amountPerItem.amount / weeks) * 1) * 100.0) / 100.0)));
                //1.12693498452012
                avgRevenueList.add(new DataSeriesItem("Average hours", avgRevenue));
                StringBuilder shortname = new StringBuilder();
                for (String s : amountPerItem.description.split(" ")) {
                    shortname.append(s.charAt(0));
                }
                categories[i++] = shortname.toString();
            }

            getConfiguration().getxAxis().setCategories(categories);
            getConfiguration().addSeries(revenueList);
            getConfiguration().addSeries(avgRevenueList);
            getConfiguration().addSeries(series2);
            series2.setyAxis(yaxis);
            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }

    public class WorkRegistrationDelayChart extends Chart {

        public WorkRegistrationDelayChart(int year) {
            setWidth("100%");
            setHeight("280px");

            setCaption("Average Work registration delay");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setType(ChartType.COLUMN);
            getConfiguration().getChart().setAnimation(true);
            getConfiguration().getxAxis().getLabels().setEnabled(true);

            getConfiguration().getxAxis().setTickWidth(0);
            getConfiguration().getyAxis().setTitle("");
            getConfiguration().getLegend().setEnabled(false);

            List<AmountPerItem> amountPerItemList = dataAccess.getWorkRegistrationDelay(year);
            double sumRevenue = 0.0;
            for (AmountPerItem amountPerItem : amountPerItemList) {
                double delay = 0.0;
                if(amountPerItem.amount>0) delay = (amountPerItem.amount / 24.0);
                sumRevenue += delay;
            }
            double avgRevenue = sumRevenue / amountPerItemList.size();

            Collections.sort(amountPerItemList);
            String[] categories = new String[amountPerItemList.size()];
            DataSeries revenueList = new DataSeries("Delay");
            DataSeries avgRevenueList = new DataSeries("Average Delay");
            PlotOptionsLine options2 = new PlotOptionsLine();
            options2.setColor(SolidColor.BLACK);
            options2.setMarker(new Marker(false));
            avgRevenueList.setPlotOptions(options2);


            int i = 0;
            for (AmountPerItem amountPerItem : amountPerItemList) {
                double delay = 0.0;
                if(amountPerItem.amount>0) delay = (amountPerItem.amount / 24.0);
                revenueList.add(new DataSeriesItem(amountPerItem.description, delay));
                avgRevenueList.add(new DataSeriesItem("Average delay", avgRevenue));
                StringBuilder shortname = new StringBuilder();
                for (String s : amountPerItem.description.split(" ")) {
                    shortname.append(s.charAt(0));
                }
                categories[i++] = shortname.toString();
            }
            getConfiguration().getxAxis().setCategories(categories);
            getConfiguration().addSeries(revenueList);
            getConfiguration().addSeries(avgRevenueList);
            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }

    public class RevenueRateChart extends Chart {

        public RevenueRateChart(int year) {
            setWidth("100%");
            setHeight("280px");

            setCaption("Revenue rate past month compared to same time last year");
            getConfiguration().getChart().setType(ChartType.SOLIDGAUGE);

            getConfiguration().getTitle().setText("Revenue Rate");

            Pane pane = new Pane();
            pane.setCenterXY("50%", "85%");
            pane.setSize("140%");
            pane.setStartAngle(-90);
            pane.setEndAngle(90);
            getConfiguration().addPane(pane);

            getConfiguration().getTooltip().setEnabled(false);

            Background bkg = new Background();
            bkg.setBackgroundColor(new SolidColor("#eeeeee"));
            bkg.setInnerRadius("60%");
            bkg.setOuterRadius("100%");
            bkg.setShape("arc");
            bkg.setBorderWidth(0);
            pane.setBackground(bkg);

            YAxis yaxis = getConfiguration().getyAxis();
            yaxis.setLineWidth(0);
            yaxis.setTickInterval(100);
            yaxis.setTickWidth(0);
            yaxis.setMin(0);
            yaxis.setMax(200);
            yaxis.setTitle("");
            yaxis.getTitle().setY(-70);
            yaxis.getLabels().setY(16);
            Stop stop1 = new Stop(0.1f, SolidColor.RED);
            Stop stop2 = new Stop(0.4f, SolidColor.YELLOW);
            Stop stop3 = new Stop(0.6f, SolidColor.GREEN);
            yaxis.setStops(stop1, stop2, stop3);

            PlotOptionsSolidGauge plotOptions = new PlotOptionsSolidGauge();
            plotOptions.getTooltip().setValueSuffix(" percent");
            Labels labels = new Labels();
            labels.setY(5);
            labels.setBorderWidth(0);
            labels.setUseHTML(true);
            labels.setFormat("<div style=\"text-align:center\"><span style=\"font-size:25px;\">{y}</span><br/>"
                    + "                       <span style=\"font-size:12pxg\">percent</span></div>");
            plotOptions.setDataLabels(labels);
            getConfiguration().setPlotOptions(plotOptions);

            final ListSeries series = new ListSeries("percent", (Math.round(dataAccess.getRevenueRate()*100.0)/100.0));
            getConfiguration().setSeries(series);

            drawChart(getConfiguration());
            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }

    public class ProjectDetailChart extends Chart {

        public ProjectDetailChart() {
            setWidth("100%");
            setHeight("280px");

            setCaption("Project Detail");
            getConfiguration().getChart().setType(ChartType.COLUMN);

            //getConfiguration().getChart().setId("chart");

            getConfiguration().setTitle("IKKE FÆRDIG");
            //getConfiguration().setSubTitle("WORK IN PROGRESS - NOTHING TO SEE HERE...");
            getConfiguration().getLegend().setEnabled(false);

            XAxis x = new XAxis();
            x.setType(AxisType.CATEGORY);
            getConfiguration().addxAxis(x);
            getConfiguration().getxAxis().getLabels().setEnabled(false);

            YAxis y = new YAxis();
            y.setTitle("Revenue");
            getConfiguration().addyAxis(y);

            PlotOptionsColumn column = new PlotOptionsColumn();
            column.setCursor(Cursor.POINTER);
            column.setDataLabels(new Labels(true));
            column.getDataLabels().setFormatter("this.y +'kkr'");

            getConfiguration().setPlotOptions(column);

            Tooltip tooltip = new Tooltip();
            tooltip.setHeaderFormat("<span style=\"font-size:11px\">{series.name}</span><br>");
            tooltip.setPointFormat("<span style=\"color:{point.color}\">{point.name}</span>: <b>{point.y:.2f}</b><br/>");
            getConfiguration().setTooltip(tooltip);


            DataSeries series = new DataSeries();
            series.setName("Project Revenue");
            PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
            plotOptionsColumn.setColorByPoint(true);
            series.setPlotOptions(plotOptionsColumn);

            for (AmountPerItem projectRevenue : dataAccess.getProjectRevenue(2015)) {
                DataSeriesItem item = new DataSeriesItem(projectRevenue.description, projectRevenue.amount);
                DataSeries drillSeries = new DataSeries(projectRevenue.description);
                PlotOptionsColumn plotOptions = new PlotOptionsColumn();
                plotOptions.setStacking(Stacking.NORMAL);
                drillSeries.setPlotOptions(plotOptions);
                drillSeries.setId(projectRevenue.description);
                String[] categories = new String[] { "Task 1", "Task 2",
                        "Task 3", "Task 4" };
                Number[] ys = new Number[] { 10.85, 7.35, 33.06, 2.81 };
                // TODO: FINISH DRILLSERIES
                //drillSeries.setData(categories, ys);
                Map<String, AmountPerItem> taskAmountPerItem = new HashMap<>();
                for (AmountPerItem amountPerItem : dataAccess.getWorkPerUserPerTaskByProject(projectRevenue.uuid)) {
                    if(!taskAmountPerItem.containsKey(amountPerItem.uuid)) taskAmountPerItem.put(amountPerItem.uuid, amountPerItem);

                    //drillSeries.add(new DataSeriesItem(amountPerItem.uuid, amountPerItem.amount));
                    //drillSeries.setStack(amountPerItem.description);
                }

                for (String taskName : taskAmountPerItem.keySet()) {

                }

                //drillSeries.(ys);




                series.addItemWithDrilldown(item, drillSeries);
            }
            getConfiguration().addSeries(series);
            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }

    /*

     */
}
