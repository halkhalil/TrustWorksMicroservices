package dk.trustworks.bimanager;

import com.codahale.metrics.servlets.MetricsServlet;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.netflix.hystrix.contrib.requestservlet.HystrixRequestContextServletFilter;
import dk.trustworks.bimanager.handler.ProjectBudgetHandler;
import dk.trustworks.bimanager.handler.ReportHandler;
import dk.trustworks.bimanager.handler.StatisticHandler;
import dk.trustworks.bimanager.handler.TaskBudgetHandler;
import dk.trustworks.bimanager.jobs.WorkItemMonthlyJob;
import dk.trustworks.bimanager.jobs.enums.Event;
import dk.trustworks.framework.BaseApplication;
import dk.trustworks.framework.servlets.MetricsServletContextListener;
import dk.trustworks.bimanager.web.Web;
import dk.trustworks.framework.persistence.Helper;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.cache.DirectBufferCache;
import io.undertow.server.handlers.resource.CachingResourceManager;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
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

import javax.servlet.DispatcherType;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by hans on 16/03/15.
 */
public class BiApplication extends BaseApplication {

    private static final Logger log = LogManager.getLogger(BiApplication.class);

    public static void main(String[] args) throws Exception {
        new BiApplication();
    }

    public BiApplication() throws Exception {
        DeploymentManager manager = getMetricsDeploymentManager();

        Undertow.builder()
                .addHttpListener(Integer.parseInt(System.getenv("BISERVICE_PORT")), System.getenv("APPLICATION_HOST"))
                .setBufferSize(1024 * 16)
                .setIoThreads(Runtime.getRuntime().availableProcessors() * 2) //this seems slightly faster in some configurations
                .setSocketOption(Options.BACKLOG, 10000)
                .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false) //don't send a keep-alive header for HTTP/1.1 requests, as it is not required
                .setServerOption(UndertowOptions.ALWAYS_SET_DATE, true)
                .setHandler(Handlers.header(Handlers.path()
                        .addPrefixPath("/api/projectbudgets", new ProjectBudgetHandler())
                        .addPrefixPath("/api/taskbudgets", new TaskBudgetHandler())
                        .addPrefixPath("/api/reports", new ReportHandler())
                        .addPrefixPath("/api/statistics", new StatisticHandler())
                        .addPrefixPath("/servlets", manager.start())
                        , Headers.SERVER_STRING, "U-tow"))
                .setWorkerThreads(200)
                .build()
                .start();

        registerInZookeeper("biservice", System.getenv("ZK_SERVER_HOST"), System.getenv("ZK_APPLICATION_HOST"), Integer.parseInt(System.getenv("BISERVICE_PORT")));
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
