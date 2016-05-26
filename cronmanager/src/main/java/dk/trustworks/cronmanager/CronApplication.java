package dk.trustworks.cronmanager;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.trustworks.cronmanager.jobs.BudgetCleanupJob;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.jooby.Jooby;
import org.jooby.json.Jackson;
import org.jooby.quartz.Quartz;

/**
 * Created by hans on 07/06/15.
 */
public class CronApplication extends Jooby {

    public static final MetricRegistry metricRegistry = new MetricRegistry();
    private transient ObjectMapper metricsMapper;

    {
        try {
            //registerInZookeeper("userservice", System.getenv("ZK_SERVER_HOST"), System.getenv("ZK_APPLICATION_HOST"), Integer.parseInt(System.getenv("ZK_APPLICATION_PORT")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        use(new Jackson());
        use(new Quartz().with(BudgetCleanupJob.class));
    }

    public static void main(final String[] args) throws Throwable {
        new CronApplication().start();
    }
    /*

    public static void main(String[] args) throws SchedulerException {
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();

        JobDetail job = newJob(BudgetCleanupJob.class)
                .withIdentity("budgetCleanupJob1", "monthlyJobs")
                .build();
        CronTrigger trigger = newTrigger()
                .withIdentity("trigger1", "monthlyJobs")
                .withSchedule(cronSchedule("0/60 * * * * ?"))
                .build();
        sched.scheduleJob(job, trigger);
        sched.start();
    }*/

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

}
