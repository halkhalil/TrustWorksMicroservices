package dk.trustworks.bimanager;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import dk.trustworks.bimanager.handler.ProjectBudgetHandler;
import dk.trustworks.bimanager.handler.ReportHandler;
import dk.trustworks.bimanager.handler.StatisticHandler;
import dk.trustworks.bimanager.handler.TaskBudgetHandler;
import dk.trustworks.bimanager.jobs.WorkItemMonthlyJob;
import dk.trustworks.bimanager.jobs.enums.Event;
import dk.trustworks.bimanager.web.Web;
import dk.trustworks.framework.persistence.Helper;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.cache.DirectBufferCache;
import io.undertow.server.handlers.resource.*;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.util.Headers;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.xnio.Options;

import java.io.File;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.servlet.Servlets.servlet;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by hans on 16/03/15.
 */
public class BiApplication {

    private static final Logger log = LogManager.getLogger(BiApplication.class);

    public static void main(String[] args) throws Exception {
        new BiApplication(Integer.parseInt(args[0]));
    }

    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyServlet extends VaadinServlet {

    }

    @Theme("valo")
    public static class MyUI extends UI {

        @Override
        protected void init(VaadinRequest request) {
            setContent(new Label("HelloWorld!"));
        }

    }

    public BiApplication(int port) throws Exception {
        log.info("LOG00800: BiManager on port " + port);
        Properties properties = new Properties();
        try (InputStream in = Helper.class.getResourceAsStream("server.properties")) {
            properties.load(in);
        }

        Undertow.builder()
                .addHttpListener(port, properties.getProperty("web.host"))
                .setBufferSize(1024 * 16)
                .setIoThreads(Runtime.getRuntime().availableProcessors() * 2) //this seems slightly faster in some configurations
                .setSocketOption(Options.BACKLOG, 10000)
                .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false) //don't send a keep-alive header for HTTP/1.1 requests, as it is not required
                .setServerOption(UndertowOptions.ALWAYS_SET_DATE, true)
                //.setHandler(resource(new ClassPathResourceManager(Web.class.getClassLoader(), "dk/trustworks/bimanager/web")).setDirectoryListingEnabled(true))
                .setHandler(Handlers.header(Handlers.path()
                                .addPrefixPath("/api/projectbudgets", new ProjectBudgetHandler())
                                .addPrefixPath("/api/taskbudgets", new TaskBudgetHandler())
                                .addPrefixPath("/api/reports", new ReportHandler())
                                .addPrefixPath("/api/statistics", new StatisticHandler())
                        , Headers.SERVER_STRING, "U-tow"))
                .setWorkerThreads(200)
                .build()
                .start();

        registerInZookeeper(properties.getProperty("zookeeper.host"), port);

        //startSchedulers();
    }

    private HttpHandler createStaticResourceHandler() {
        final ResourceManager staticResources =
                new ClassPathResourceManager(Web.class.getClassLoader());
        // Cache tuning is copied from Undertow unit tests.
        final ResourceManager cachedResources =
                new CachingResourceManager(100, 65536,
                        new DirectBufferCache(1024, 10, 10480),
                        staticResources,
                        (int) Duration.ofDays(1).getSeconds());
        final ResourceHandler resourceHandler = new ResourceHandler(cachedResources);
        resourceHandler.setWelcomeFiles("index.html");
        resourceHandler.setDirectoryListingEnabled(true);
        return resourceHandler;
    }

    private final void startSchedulers() throws SchedulerException {
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

    private static void registerInZookeeper(String zooHost, int port) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zooHost + ":2181", new RetryNTimes(5, 1000));
        curatorFramework.start();

        ServiceInstance serviceInstance = ServiceInstance.builder()
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .address("localhost")
                .port(port)
                .name("biservice")
                .build();

        ServiceDiscoveryBuilder.builder(Object.class)
                .basePath("trustworks")
                .client(curatorFramework)
                .thisInstance(serviceInstance)
                .build()
                .start();
    }
}
