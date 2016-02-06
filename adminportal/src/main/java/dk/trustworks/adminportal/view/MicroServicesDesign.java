package dk.trustworks.adminportal.view;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.declarative.Design;
import dk.trustworks.adminportal.domain.DataAccess;
import org.json.JSONObject;

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
public class MicroServicesDesign extends CssLayout {
    private final DataAccess dataAccess = new DataAccess();

	protected CssLayout dashboard_item5;
	protected CssLayout dashboard_item26;
	protected CssLayout dashboard_item27;
    protected CssLayout dashboard_item28;
    /*protected CssLayout dashboard_item29;
    protected CssLayout dashboard_item30;
    protected CssLayout dashboard_item31;
    protected CssLayout dashboard_item32;*/
    protected HorizontalLayout sparkline_horizontal;

	public MicroServicesDesign() {
		Design.read(this);




        createGraphs(Calendar.getInstance().get(Calendar.YEAR));

        //sparkline_horizontal.addComponent(new SparklineChart("income per day", "kkr", "today ", new SolidColor("#AAAA00"), intArray));

    }

    private void createGraphs(int year) {
        WorkRegistrationDelayChart workRegistrationDelayChart = new WorkRegistrationDelayChart(year);
        //dashboard_item30.removeAllComponents();
        //dashboard_item30.addComponent(workRegistrationDelayChart);

        BiManagerTimersChart topGrossingEmployeesChart = new BiManagerTimersChart();
        dashboard_item5.removeAllComponents();
        dashboard_item5.addComponent(topGrossingEmployeesChart);

        UserManagerTimersChart topGrossingProjectsChart = new UserManagerTimersChart();
        dashboard_item26.removeAllComponents();
        dashboard_item26.addComponent(topGrossingProjectsChart);

        RevenuePerMonthChart revenuePerMonthChart = new RevenuePerMonthChart(year);
        dashboard_item27.removeAllComponents();
        dashboard_item27.addComponent(revenuePerMonthChart);

        RevenuePerMonthByCapacityChart revenuePerMonthByCapacityChart = new RevenuePerMonthByCapacityChart();
        dashboard_item28.removeAllComponents();
        dashboard_item28.addComponent(revenuePerMonthByCapacityChart);

        BillableHoursPerEmployeesChart billableHoursPerEmployeesChart = new BillableHoursPerEmployeesChart(year);
        //dashboard_item29.removeAllComponents();
        //dashboard_item29.addComponent(billableHoursPerEmployeesChart);

        RevenueRateChart revenueRateChart = new RevenueRateChart(year);
        //dashboard_item31.removeAllComponents();
        //dashboard_item31.addComponent(revenueRateChart);

        ProjectDetailChart projectDetailChart = new ProjectDetailChart();
        //dashboard_item32.removeAllComponents();
        //dashboard_item32.addComponent(projectDetailChart);
    }

    public class BiManagerTimersChart extends Chart {

        public BiManagerTimersChart() {
            setWidth("100%");  // 100% by default
            setHeight("280px"); // 400px by default
            //setSizeFull();

            setCaption("BiService Response Times");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setType(ChartType.COLUMN);
            getConfiguration().getChart().setAnimation(true);
            getConfiguration().getxAxis().getLabels().setEnabled(false);
            getConfiguration().getxAxis().setTickWidth(0);
            getConfiguration().getyAxis().setTitle("");
            getConfiguration().getLegend().setEnabled(false);

            JSONObject statisticMetrics = dataAccess.getBiServiceMetrics();
            DataSeries listSeries = new DataSeries("Response Times");

            DataSeries temperatureErrors = new DataSeries("Timer deviation");
            //getConfiguration().addSeries(temperatureErrors);
            PlotOptionsErrorBar tempErrorOptions = new PlotOptionsErrorBar();
            SolidColor green = new SolidColor("green");
            tempErrorOptions.setStemColor(green);
            tempErrorOptions.setWhiskerColor(green);
            temperatureErrors.setPlotOptions(tempErrorOptions);

            DataSeries hitsSeries = new DataSeries("Hits");

            YAxis yaxis = new YAxis();
            yaxis.setTitle("Hits");
            yaxis.setOpposite(true);
            yaxis.setMin(0);
            getConfiguration().addyAxis(yaxis);

            PlotOptionsLine options3 = new PlotOptionsLine();
            options3.setColor(SolidColor.RED);
            hitsSeries.setPlotOptions(options3);

            for (Object timer : statisticMetrics.getJSONObject("timers").keySet()) {
                DataSeriesItem item = new DataSeriesItem(timer.toString(), Double.parseDouble(statisticMetrics.getJSONObject("timers").getJSONObject(timer.toString()).get("mean").toString()));
                listSeries.add(item);
                item = new DataSeriesItem();
                item.setLow(Double.parseDouble(statisticMetrics.getJSONObject("timers").getJSONObject(timer.toString()).get("min").toString()));
                item.setHigh(Double.parseDouble(statisticMetrics.getJSONObject("timers").getJSONObject(timer.toString()).get("max").toString()));
                temperatureErrors.add(item);
                item = new DataSeriesItem(timer.toString(), Double.parseDouble(statisticMetrics.getJSONObject("timers").getJSONObject(timer.toString()).get("count").toString()));
                hitsSeries.add(item);
            }
            getConfiguration().addSeries(listSeries);
            getConfiguration().addSeries(hitsSeries);
            hitsSeries.setyAxis(yaxis);
            //getConfiguration().addSeries(temperatureErrors);
            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }

    public class UserManagerTimersChart extends Chart {

        public UserManagerTimersChart() {
            setWidth("100%");  // 100% by default
            setHeight("280px"); // 400px by default
            //setSizeFull();

            setCaption("UserService Response Times");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setType(ChartType.COLUMN);
            getConfiguration().getChart().setAnimation(true);
            getConfiguration().getxAxis().getLabels().setEnabled(false);
            getConfiguration().getxAxis().setTickWidth(0);
            getConfiguration().getyAxis().setTitle("");
            getConfiguration().getLegend().setEnabled(false);

            JSONObject statisticMetrics = dataAccess.getUserServiceMetrics();
            DataSeries listSeries = new DataSeries("Response Times");

            DataSeries temperatureErrors = new DataSeries("Timer deviation");
            //getConfiguration().addSeries(temperatureErrors);
            PlotOptionsErrorBar tempErrorOptions = new PlotOptionsErrorBar();
            SolidColor green = new SolidColor("green");
            tempErrorOptions.setStemColor(green);
            tempErrorOptions.setWhiskerColor(green);
            temperatureErrors.setPlotOptions(tempErrorOptions);

            DataSeries hitsSeries = new DataSeries("Hits");

            YAxis yaxis = new YAxis();
            yaxis.setTitle("Hits");
            yaxis.setOpposite(true);
            yaxis.setMin(0);
            getConfiguration().addyAxis(yaxis);

            PlotOptionsLine options3 = new PlotOptionsLine();
            options3.setColor(SolidColor.RED);
            hitsSeries.setPlotOptions(options3);

            for (Object timer : statisticMetrics.getJSONObject("timers").keySet()) {
                DataSeriesItem item = new DataSeriesItem(timer.toString(), Double.parseDouble(statisticMetrics.getJSONObject("timers").getJSONObject(timer.toString()).get("mean").toString()));
                listSeries.add(item);
                item = new DataSeriesItem();
                item.setLow(Double.parseDouble(statisticMetrics.getJSONObject("timers").getJSONObject(timer.toString()).get("min").toString()));
                item.setHigh(Double.parseDouble(statisticMetrics.getJSONObject("timers").getJSONObject(timer.toString()).get("max").toString()));
                temperatureErrors.add(item);
                item = new DataSeriesItem(timer.toString(), Double.parseDouble(statisticMetrics.getJSONObject("timers").getJSONObject(timer.toString()).get("count").toString()));
                hitsSeries.add(item);
            }
            getConfiguration().addSeries(listSeries);
            getConfiguration().addSeries(hitsSeries);
            hitsSeries.setyAxis(yaxis);
            //getConfiguration().addSeries(temperatureErrors);
            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }

    public class RevenuePerMonthChart extends Chart {

        public RevenuePerMonthChart(int year) {
            setWidth("100%");  // 100% by default
            setHeight("280px"); // 400px by default
            //setSizeFull();

            setCaption("TimeService Response Times");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setType(ChartType.COLUMN);
            getConfiguration().getChart().setAnimation(true);
            getConfiguration().getxAxis().getLabels().setEnabled(false);
            getConfiguration().getxAxis().setTickWidth(0);
            getConfiguration().getyAxis().setTitle("");
            getConfiguration().getLegend().setEnabled(false);

            JSONObject statisticMetrics = dataAccess.getTimeServiceMetrics();
            DataSeries listSeries = new DataSeries("Response Times");

            DataSeries temperatureErrors = new DataSeries("Timer deviation");
            //getConfiguration().addSeries(temperatureErrors);
            PlotOptionsErrorBar tempErrorOptions = new PlotOptionsErrorBar();
            SolidColor green = new SolidColor("green");
            tempErrorOptions.setStemColor(green);
            tempErrorOptions.setWhiskerColor(green);
            temperatureErrors.setPlotOptions(tempErrorOptions);

            DataSeries hitsSeries = new DataSeries("Hits");

            YAxis yaxis = new YAxis();
            yaxis.setTitle("Hits");
            yaxis.setOpposite(true);
            yaxis.setMin(0);
            getConfiguration().addyAxis(yaxis);

            PlotOptionsLine options3 = new PlotOptionsLine();
            options3.setColor(SolidColor.RED);
            hitsSeries.setPlotOptions(options3);

            for (Object timer : statisticMetrics.getJSONObject("timers").keySet()) {
                DataSeriesItem item = new DataSeriesItem(timer.toString(), Double.parseDouble(statisticMetrics.getJSONObject("timers").getJSONObject(timer.toString()).get("mean").toString()));
                listSeries.add(item);
                item = new DataSeriesItem();
                item.setLow(Double.parseDouble(statisticMetrics.getJSONObject("timers").getJSONObject(timer.toString()).get("min").toString()));
                item.setHigh(Double.parseDouble(statisticMetrics.getJSONObject("timers").getJSONObject(timer.toString()).get("max").toString()));
                temperatureErrors.add(item);
                item = new DataSeriesItem(timer.toString(), Double.parseDouble(statisticMetrics.getJSONObject("timers").getJSONObject(timer.toString()).get("count").toString()));
                hitsSeries.add(item);
            }
            getConfiguration().addSeries(listSeries);
            getConfiguration().addSeries(hitsSeries);
            hitsSeries.setyAxis(yaxis);
            //getConfiguration().addSeries(temperatureErrors);
            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }

    public class RevenuePerMonthByCapacityChart extends Chart {

        public RevenuePerMonthByCapacityChart() {
            setWidth("100%");  // 100% by default
            setHeight("280px"); // 400px by default
            //setSizeFull();

            setCaption("ClientService Response Times");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setType(ChartType.COLUMN);
            getConfiguration().getChart().setAnimation(true);
            getConfiguration().getxAxis().getLabels().setEnabled(false);
            getConfiguration().getxAxis().setTickWidth(0);
            getConfiguration().getyAxis().setTitle("");
            getConfiguration().getLegend().setEnabled(false);

            JSONObject statisticMetrics = dataAccess.getClientServiceMetrics();
            DataSeries listSeries = new DataSeries("Response Times");

            DataSeries temperatureErrors = new DataSeries("Timer deviation");
            //getConfiguration().addSeries(temperatureErrors);
            PlotOptionsErrorBar tempErrorOptions = new PlotOptionsErrorBar();
            SolidColor green = new SolidColor("green");
            tempErrorOptions.setStemColor(green);
            tempErrorOptions.setWhiskerColor(green);
            temperatureErrors.setPlotOptions(tempErrorOptions);

            DataSeries hitsSeries = new DataSeries("Hits");

            YAxis yaxis = new YAxis();
            yaxis.setTitle("Hits");
            yaxis.setOpposite(true);
            yaxis.setMin(0);
            getConfiguration().addyAxis(yaxis);

            PlotOptionsLine options3 = new PlotOptionsLine();
            options3.setColor(SolidColor.RED);
            hitsSeries.setPlotOptions(options3);

            for (Object timer : statisticMetrics.getJSONObject("timers").keySet()) {
                DataSeriesItem item = new DataSeriesItem(timer.toString(), Double.parseDouble(statisticMetrics.getJSONObject("timers").getJSONObject(timer.toString()).get("mean").toString()));
                listSeries.add(item);
                item = new DataSeriesItem();
                item.setLow(Double.parseDouble(statisticMetrics.getJSONObject("timers").getJSONObject(timer.toString()).get("min").toString()));
                item.setHigh(Double.parseDouble(statisticMetrics.getJSONObject("timers").getJSONObject(timer.toString()).get("max").toString()));
                temperatureErrors.add(item);
                item = new DataSeriesItem(timer.toString(), Double.parseDouble(statisticMetrics.getJSONObject("timers").getJSONObject(timer.toString()).get("count").toString()));
                hitsSeries.add(item);
            }
            getConfiguration().addSeries(listSeries);
            getConfiguration().addSeries(hitsSeries);
            hitsSeries.setyAxis(yaxis);
            //getConfiguration().addSeries(temperatureErrors);
            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }

    public class BillableHoursPerEmployeesChart extends Chart {

        public BillableHoursPerEmployeesChart(int year) {
        }
    }

    public class WorkRegistrationDelayChart extends Chart {

        public WorkRegistrationDelayChart(int year) {
        }
    }

    public class RevenueRateChart extends Chart {

        public RevenueRateChart(int year) {
        }
    }

    public class ProjectDetailChart extends Chart {

        public ProjectDetailChart() {

        }
    }
}
