package dk.trustworks.usermanager;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.trustworks.usermanager.service.SalaryService;
import dk.trustworks.usermanager.service.UserService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.jooby.Jooby;
import org.jooby.jdbc.Jdbc;
import org.jooby.json.Jackson;
import org.jooby.raml.Raml;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by hans on 16/03/15.
 */
public class UserApplication extends Jooby {

    public static final MetricRegistry metricRegistry = new MetricRegistry();
    private transient ObjectMapper metricsMapper;

    {
        this.metricsMapper = new ObjectMapper().registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, true));

        try {
            registerInZookeeper("userservice", System.getProperty("zookeeper.host"), System.getProperty("application.host"), Integer.parseInt(System.getProperty("application.port")));
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        System.setProperty("db.url", System.getenv("DATABASE_URI"));
        System.setProperty("db.user", System.getenv("DATABASE_USER"));
        System.setProperty("db.password", System.getenv("DATABASE_PASS"));
        System.setProperty("application.port", System.getenv("USERSERVICE_PORT"));
        System.setProperty("application.host", System.getenv("APPLICATION_URL"));
*/
        use(new Jdbc());
        use(new Jackson());
        //use(new Metrics().request());
        //SwaggerUI.install(this);


        /**
         *
         * Everything about your users.
         */
        use("/api/users")
            .get("/", (req, resp) -> {
                final Timer timer = metricRegistry.timer(name("user", "all", "response"));
                final Timer.Context context = timer.time();
                try {
                    DataSource db = req.require(DataSource.class);
                    resp.send(new UserService(db).findAll());
                } finally {
                    context.stop();
                }
            })//.name("Get all users");

            .get("/:uuid", (req, resp) -> {
                final Timer timer = metricRegistry.timer(name("user", "get", "response"));
                final Timer.Context context = timer.time();
                try {
                    String uuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(new UserService(db).findByUUID(uuid));
                } finally {
                    context.stop();
                }
            })//.name("Get all users");

            .get("/search/findByActiveTrue", (req, resp) -> {
                final Timer timer = metricRegistry.timer(name("user", "search", "findByActiveTrue", "response"));
                final Timer.Context context = timer.time();
                try {
                    DataSource db = req.require(DataSource.class);
                    resp.send(new UserService(db).findByActiveTrue());
                } finally {
                    context.stop();
                }
            })//.name("findByUsernameAndPasswordAndActiveTrue");

            .get("/search/findByUsername", (req, resp) -> {
                final Timer timer = metricRegistry.timer(name("user", "search", "findByActiveTrue", "response"));
                final Timer.Context context = timer.time();
                try {
                    String username = req.param("username").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(new UserService(db).findByUsername(username));
                } finally {
                    context.stop();
                }
            })

            .get("/search/findByUsernameAndPasswordAndActiveTrue", (req, resp) -> {
                final Timer timer = metricRegistry.timer(name("user", "search", "findByUsernameAndPasswordAndActiveTrue", "response"));
                final Timer.Context context = timer.time();
                try {
                    String username = req.param("username").value();
                    String password = req.param("password").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(new UserService(db).findByUsernameAndPasswordAndActiveTrue(username, password));
                } finally {
                    context.stop();
                }
            })//).name("findByUsernameAndPasswordAndActiveTrue");

            .get("/command/capacitypermonth", (req, resp) -> {
                final Timer timer = metricRegistry.timer(name("user", "command", "capacitypermonth", "response"));
                final Timer.Context context = timer.time();
                try {
                    int year = req.param("year").intValue();
                    DataSource db = req.require(DataSource.class);
                    resp.send(new UserService(db).capacitypermonth(year));
                } finally {
                    context.stop();
                }
            })

            .get("/command/capacitypermonthbyuser", (req, resp) -> {
                final Timer timer = metricRegistry.timer(name("user", "command", "capacitypermonthbyuser", "response"));
                final Timer.Context context = timer.time();
                try {
                    int year = req.param("year").intValue();
                    String userUUID = req.param("useruuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(new UserService(db).capacitypermonthbyuser(year, userUUID));
                } finally {
                    context.stop();
                }
            })

            .get("/command/useravailabilitypermonthbyyear", (req, resp) -> {
                final Timer timer = metricRegistry.timer(name("user", "command", "useravailabilitypermonthbyyear", "response"));
                final Timer.Context context = timer.time();
                try {
                    int year = req.param("year").intValue();
                    boolean fiscal = false;
                    if(req.param("fiscal")!=null) fiscal = Boolean.parseBoolean(req.param("fiscal").value());
                    DataSource db = req.require(DataSource.class);
                    resp.send(new UserService(db).useravailabilitypermonthbyyear(year, fiscal));
                } finally {
                    context.stop();
                }
            })
                .produces("json")
                .consumes("json");;//.name("useravailabilitypermonthbyyear");

        use("/api/salaries")
            .get("/search/findActiveByDate", (req, resp) -> {
                final Timer timer = metricRegistry.timer(name("salary", "search", "findActiveByDate", "response"));
                final Timer.Context context = timer.time();
                try {
                    DateTime date = DateTime.parse(req.param("date").value(), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    DataSource db = req.require(DataSource.class);
                    resp.send(new SalaryService(db).findActiveByDate(date));
                } finally {
                    context.stop();
                }
            })

            .get("/command/usersalarypermonthbyyear", (req, resp) -> {
                final Timer timer = metricRegistry.timer(name("salary", "command", "usersalarypermonthbyyear", "response"));
                final Timer.Context context = timer.time();
                try {
                    int year = req.param("year").intValue();
                    boolean fiscal = false;
                    if(req.param("fiscal")!=null) fiscal = Boolean.parseBoolean(req.param("fiscal").value());
                    DataSource db = req.require(DataSource.class);
                    resp.send(new SalaryService(db).usersalarypermonthbyyear(year, fiscal));
                } finally {
                    context.stop();
                }
            })
                    .produces("json")
                    .consumes("json");

        use("/servlets/metrics")
                .get("/", (req, resp) -> {
                    resp.send(metricsMapper.valueToTree(metricRegistry));
                });


        new Raml().install(this);
        //new SwaggerUI().install(this);
        /*
        use(new Metrics()
                .request()
                .threadDump()
                .ping()
                .metric("memory", new MemoryUsageGaugeSet())
                .metric("threads", new ThreadStatesGaugeSet())
                .metric("gc", new GarbageCollectorMetricSet())
                .metric("fs", new FileDescriptorRatioGauge())
        );
        */
    }

    public static void main(final String[] args) throws Throwable {
        new UserApplication().start();
    }

                        //.addPrefixPath("/api/users", new UserHandler())
                        //.addPrefixPath("/api/salaries", new SalaryHandler())

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
