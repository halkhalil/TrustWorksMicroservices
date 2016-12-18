package dk.trustworks.bimanager;

import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import dk.trustworks.bimanager.dto.numerics.Data;
import dk.trustworks.bimanager.dto.numerics.GraphWidget;
import dk.trustworks.bimanager.dto.numerics.NumberWidget;
import dk.trustworks.bimanager.jobs.WorkItemMonthlyJob;
import dk.trustworks.bimanager.jobs.enums.Event;
import dk.trustworks.framework.security.JwtModule;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.joda.time.LocalDate;
import org.jooby.Jooby;
import org.jooby.RequestLogger;
import org.jooby.Result;
import org.jooby.Results;
import org.jooby.jdbc.Jdbc;
import org.jooby.json.Jackson;
import org.jooby.metrics.Metrics;
import org.jooby.swagger.SwaggerUI;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;


import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by hans on 16/03/15.
 */
public class BiApplication extends Jooby {
    private static Result HOME = Results
            .ok(
                    "<!doctype html>\n" +
                            "<html lang=\"en\">\n" +
                            "<head>\n" +
                            "  <title>Jooby API tools</title>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "<h1>Jooby API tools demo</h1>\n" +
                            "<ul>\n" +
                            "<li>Pets API with <a href=\"/raml\">raml</a></li>\n" +
                            "<li>Pets API with <a href=\"/swagger\">swagger</a></li>\n" +
                            "</ul>\n" +
                            "<p>More at <a href=\"http://jooby.org/doc/spec\">" +
                            "http://jooby.org/doc/spec</a>\n" +
                            "</body>\n" +
                            "</html>")
            .type("html");

    {
        use("*", new RequestLogger());

        try {
            registerInZookeeper("biservice", System.getProperty("zookeeper.host"), System.getProperty("application.host"), Integer.parseInt(System.getProperty("application.port")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        use(new Jdbc());
        use(new Jackson().module(new JodaModule()));

        get("/", () -> HOME);

        on("dev", () -> use(new JwtModule(false)))
                .orElse(() -> use(new JwtModule(true)));

        use("/api/numerics")
                .get("/monthgrossincome", (req, resp) -> {
                    double monthRevenue = 0;
                    DataSource db = req.require(DataSource.class);
                    try (Connection con = new Sql2o(db).open()) {
                        monthRevenue = con.createQuery("SELECT SUM(w.workduration * twc.price) " +
                                "FROM timemanager.work_latest w " +
                                "INNER JOIN usermanager.user u ON w.useruuid = u.uuid " +
                                "INNER JOIN clientmanager.taskworkerconstraint twc ON twc.taskuuid = w.taskuuid AND twc.useruuid = u.uuid " +
                                "WHERE ((w.year*10000)+((w.month+1)*100)+w.day) between :periodStart and :periodEnd;")
                                .addParameter("periodStart", (Integer.parseInt(LocalDate.now().withDayOfMonth(1).toString("yyyyMMdd"))-1))
                                .addParameter("periodEnd", (Integer.parseInt(LocalDate.now().withDayOfMonth(LocalDate.now().dayOfMonth().getMaximumValue()).toString("yyyyMMdd"))+1))
                                .executeScalar(Double.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    resp.send(new NumberWidget("Gross income "+LocalDate.now().monthOfYear().getAsText(), Math.round(monthRevenue)));
                })
                .get("/revenuepermonth", (req, resp) -> {
                    List<Double> monthRevenue = new ArrayList<>();
                    DataSource db = req.require(DataSource.class);
                    try (Connection con = new Sql2o(db).open()) {
                        monthRevenue = con.createQuery("SELECT SUM(w.workduration * twc.price) revenue FROM timemanager.work_latest w " +
                                "INNER JOIN usermanager.user u ON w.useruuid = u.uuid " +
                                "INNER JOIN clientmanager.taskworkerconstraint twc ON twc.taskuuid = w.taskuuid AND twc.useruuid = u.uuid " +
                                "WHERE ((w.year*10000)+((w.month+1)*100)+w.day) between :periodStart and :periodEnd " +
                                "GROUP BY w.year, w.month ORDER BY w.year, w.month;")
                                .addParameter("periodStart", LocalDate.now().getYear()+"0700")
                                .addParameter("periodEnd", LocalDate.now().plusYears(1).getYear()+"0631")
                                .executeAndFetch(Double.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    GraphWidget graphWidget = new GraphWidget("Month Revenue " + LocalDate.now().getYear());
                    LocalDate localDate = LocalDate.now().withMonthOfYear(7).withDayOfMonth(1);
                    for (Double integer : monthRevenue) {
                        graphWidget.data.add(new Data(Math.round(integer), localDate.monthOfYear().getAsText()));
                        localDate = localDate.plusMonths(1);
                    }

                    resp.send(graphWidget);
                })
                .get("/revenueperclient", (req, resp) -> {
                    List<Map<String, Object>> monthRevenue = new ArrayList<>();
                    DataSource db = req.require(DataSource.class);
                    try (Connection con = new Sql2o(db).open()) {
                        monthRevenue = con.createQuery("SELECT c.name name, SUM(w.workduration * twc.price) total " +
                                "FROM timemanager.work_latest w " +
                                "INNER JOIN usermanager.user u ON w.useruuid = u.uuid " +
                                "INNER JOIN clientmanager.taskworkerconstraint twc ON twc.taskuuid = w.taskuuid AND twc.useruuid = u.uuid " +
                                "INNER JOIN clientmanager.task t ON w.taskuuid = t.uuid " +
                                "INNER JOIN clientmanager.project p ON t.projectuuid = p.uuid " +
                                "INNER JOIN clientmanager.client c ON p.clientuuid = c.uuid " +
                                "WHERE ((w.year*10000)+((w.month+1)*100)+w.day) between :periodStart and :periodEnd " +
                                "GROUP BY c.uuid " +
                                "ORDER BY total DESC " +
                                "LIMIT 10;")
                                .addParameter("periodStart", LocalDate.now().getYear()+"0700")
                                .addParameter("periodEnd", LocalDate.now().plusYears(1).getYear()+"0631")
                                .executeAndFetchTable().asList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    GraphWidget graphWidget = new GraphWidget("Revenue Per Client " + LocalDate.now().getYear());
                    //LocalDate localDate = LocalDate.now().withMonthOfYear(7).withDayOfMonth(1);
                    for (Map<String, Object> value : monthRevenue) {
                        graphWidget.data.add(new Data(Math.round((double)value.get("total")), (String)value.get("name")));
                        //localDate = localDate.plusMonths(1);
                    }

                    resp.send(graphWidget);
                })
                .get("/revenueperday", (req, resp) -> {
                    List<Double> monthRevenue = new ArrayList<>();
                    DataSource db = req.require(DataSource.class);
                    try (Connection con = new Sql2o(db).open()) {
                        monthRevenue = con.createQuery("SELECT SUM(w.workduration * twc.price) revenue FROM timemanager.work_latest w " +
                                "INNER JOIN usermanager.user u ON w.useruuid = u.uuid " +
                                "INNER JOIN clientmanager.taskworkerconstraint twc ON twc.taskuuid = w.taskuuid AND twc.useruuid = u.uuid " +
                                "WHERE ((w.year*10000)+((w.month+1)*100)+w.day) between :periodStart and :periodEnd " +
                                "GROUP BY w.year, w.month, w.day ORDER BY w.year, w.month, w.day;")
                                .addParameter("periodStart", LocalDate.now().minusMonths(1).toString("yyyyMMdd"))
                                .addParameter("periodEnd", LocalDate.now().toString("yyyyMMdd"))
                                .executeAndFetch(Double.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    GraphWidget graphWidget = new GraphWidget("Revenue per Day" + LocalDate.now().getYear());
                    LocalDate localDate = LocalDate.now().withDayOfMonth(1);
                    for (Double integer : monthRevenue) {
                        graphWidget.data.add(new Data(Math.round(integer), localDate));
                        localDate = localDate.plusDays(1);
                    }
                    System.out.println("graphWidget = " + graphWidget);
                    resp.send(graphWidget);
                })

                .produces("json")
                .consumes("json");


        use(new Metrics()
                .request()
                .threadDump()
                .ping()
                .metric("memory", new MemoryUsageGaugeSet())
                .metric("threads", new ThreadStatesGaugeSet())
                .metric("gc", new GarbageCollectorMetricSet())
                .metric("fs", new FileDescriptorRatioGauge())
        );

        new SwaggerUI().install(this);
    }

    public static void main(final String[] args) throws Throwable {
        new BiApplication().start();
    }

    protected static void registerInZookeeper(String serviceName, String zooHost, String appHost, int port) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zooHost + ":2181", new RetryNTimes(5, 1000));
        curatorFramework.start();

        ServiceInstance serviceInstance = ServiceInstance.builder()
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .address(appHost)
                .port(port)
                .name(serviceName)
                .build();

        ServiceDiscoveryBuilder.builder(Object.class)
                .basePath("trustworks")
                .client(curatorFramework)
                .thisInstance(serviceInstance)
                .build()
                .start();
    }

    private void startSchedulers() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail workItemRebaseJob = newJob(WorkItemMonthlyJob.class)
                .withIdentity("WorkItemRebaseJob", "BiManagerJobs")
                .usingJobData("event", Event.HISTORIC.name())
                .build();

        Trigger monthlyTrigger = newTrigger()
                .withIdentity("MonthlyTrigger", "BiManagerTriggers")
                //.withSchedule(cronSchedule("0 0 10am 1 * ?"))
                .withSchedule(cronSchedule("1 * * * * ?"))
                .build();

        scheduler.scheduleJob(workItemRebaseJob, monthlyTrigger);

        JobDetail workItemCurrentMonthJob = newJob(WorkItemMonthlyJob.class)
                .withIdentity("WorkItemCurrentMonthJob", "BiManagerJobs")
                .usingJobData("event", Event.CURRENTMONTH.name())
                .build();

        Trigger dailyTrigger = newTrigger()
                .withIdentity("DailyTrigger", "BiManagerTriggers")
                //.withSchedule(cronSchedule("0 0 6 ? * *"))
                .withSchedule(cronSchedule("0/20 * * * * ?"))
                .build();

        scheduler.scheduleJob(workItemCurrentMonthJob, dailyTrigger);

        scheduler.start();
    }
}
