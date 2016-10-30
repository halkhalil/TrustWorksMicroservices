package dk.trustworks.usermanager;

import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import dk.trustworks.usermanager.security.JwtModule;
import dk.trustworks.usermanager.security.UserRoles;
import dk.trustworks.usermanager.service.SalaryService;
import dk.trustworks.usermanager.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.util.Json;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.jooby.*;
import org.jooby.jdbc.Jdbc;
import org.jooby.json.Jackson;
import org.jooby.metrics.Metrics;
import org.jooby.raml.Raml;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by hans on 16/03/15.
 */
public class UserApplication extends Jooby {

    public static final String KEY = "2b393761-fd50-4c54-8d41-61bcb17cf173";

    //public static final MetricRegistry metricRegistry = new MetricRegistry();
    //private transient ObjectMapper metricsMapper;
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
        new Raml().install(this);
        use("*", new RequestLogger());

        //this.metricsMapper = new ObjectMapper().registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, true));

        try {
            registerInZookeeper("userservice", System.getProperty("zookeeper.host"), System.getProperty("application.host"), Integer.parseInt(System.getProperty("application.port")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        use(new Jdbc());
        use(new Jackson().module(new JodaModule()));
        //use(new Json());

        get("/", () -> HOME);

        //Key key = MacProvider.generateKey();

        use("/login")
                .get("/", (req, resp) -> {
                    String username = req.param("username").value("");
                    String password = req.param("password").value("");

                    DataSource db = req.require(DataSource.class);
                    List<String> roles = new UserService(db).getUserRoles(username, password);

                    //System.out.println("key = " + key.getEncoded().toString());
                    String compactJws = Jwts.builder()
                            .setIssuer("trustworks")
                            .setSubject(username)
                            .setAudience("client")
                            .setExpiration(LocalDate.now().plusDays(1).toDate())
                            .setIssuedAt(LocalDate.now().toDate())
                            .claim("roles", roles)
                            .signWith(SignatureAlgorithm.HS512, KEY)
                            .compact();
                    resp.send(compactJws);
                })
                .produces("json")
                .consumes("json");

        on("dev", () -> {
            use(new JwtModule());
        });

        use("/api/users")
                .get("/", (req, resp) -> {
                    if(!((UserRoles)req.get("roles")).hasRole(req.route().attr("role"))) throw new Err(403);
                    System.out.println("req.require(UserRoles.class).toString() = " + req.require(UserRoles.class).toString());;

                    try {
                        DataSource db = req.require(DataSource.class);
                        resp.send(new UserService(db).findAll());
                    } finally {
                        //context.stop();
                    }
                }).attr("role", "tm.user")

                .get("/:uuid", (req, resp) -> {
                    String uuid = req.param("uuid").value();

                    //final Timer timer = metricRegistry.timer(name("user", "get", "response"));
                    //final Timer.Context context = timer.time();
                    try {
                        DataSource db = req.require(DataSource.class);
                        resp.send(new UserService(db).findByUUID(uuid));
                    } finally {
                        //context.stop();
                    }
                }).attr("role", "tm.user")

                .get("/{uuid}/capacities", (req, resp) -> {
                    //int year = req.param("year").intValue(2016);
                    LocalDate periodStart = LocalDate.parse(req.param("periodStart").value("2016-01-01"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEnd = LocalDate.parse(req.param("periodEnd").value("2016-12-31"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    String userUUID = req.param("uuid").value();

                    //final Timer timer = metricRegistry.timer(name("user", "capacity", "response"));
                    //final Timer.Context context = timer.time();
                    try {

                        DataSource db = req.require(DataSource.class);
                        resp.send(new UserService(db).capacitypermonthbyuser(userUUID, periodStart, periodEnd));
                    } finally {
                        //context.stop();
                    }
                }).attr("role", "tm.admin")

                .get("/{uuid}/availabilities", (req, resp) -> {
                    LocalDate periodStart = LocalDate.parse(req.param("periodStart").value("2016-01-01"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEnd = LocalDate.parse(req.param("periodEnd").value("2016-12-31"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    String userUUID = req.param("uuid").value();

                    //final Timer timer = metricRegistry.timer(name("user", "availability", "response"));
                    //final Timer.Context context = timer.time();
                    try {
                        DataSource db = req.require(DataSource.class);
                        resp.send(new UserService(db).useravailabilitypermonthbyyearbyuser(userUUID, periodStart, periodEnd));
                    } finally {
                        //context.stop();
                    }
                }).attr("role", "tm.user")

                .get("/{uuid}/salaries", (req, resp) -> {
                    LocalDate periodStart = LocalDate.parse(req.param("periodStart").value("2016-01-01"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEnd = LocalDate.parse(req.param("periodEnd").value("2016-12-31"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    String userUUID = req.param("uuid").value();

                    //final Timer timer = metricRegistry.timer(name("users", "salary", "response"));
                    //final Timer.Context context = timer.time();
                    try {
                        DataSource db = req.require(DataSource.class);
                        resp.send(new SalaryService(db).usersalarypermonthbyyearbyuser(userUUID, periodStart, periodEnd));
                    } finally {
                        //context.stop();
                    }
                }).attr("role", "tm.admin")

                .get("/search/findByActiveTrue", (req, resp) -> {
                    //final Timer timer = metricRegistry.timer(name("user", "search", "findByActiveTrue", "response"));
                    //final Timer.Context context = timer.time();
                    try {
                        DataSource db = req.require(DataSource.class);
                        resp.send(new UserService(db).findByActiveTrue());
                    } finally {
                        //context.stop();
                    }
                }).attr("role", "tm.user")


                .get("/search/findByUsername", (req, resp) -> {
                    String username = req.param("username").value();

                    //final Timer timer = metricRegistry.timer(name("user", "search", "findByActiveTrue", "response"));
                    //final Timer.Context context = timer.time();
                    try {
                        DataSource db = req.require(DataSource.class);
                        resp.send(new UserService(db).findByUsername(username));
                    } finally {
                        //context.stop();
                    }
                }).attr("role", "tm.user")

                .get("/search/findByUsernameAndPasswordAndActiveTrue", (req, resp) -> {
                    String username = req.param("username").value();
                    String password = req.param("password").value();

                    //final Timer timer = metricRegistry.timer(name("user", "search", "findByUsernameAndPasswordAndActiveTrue", "response"));
                    //final Timer.Context context = timer.time();
                    try {
                        DataSource db = req.require(DataSource.class);
                        resp.send(new UserService(db).findByUsernameAndPasswordAndActiveTrue(username, password));
                    } finally {
                        //context.stop();
                    }
                }).attr("role", "tm.user")

                .produces("json")
                .consumes("json");

        /**
         *
         * Everything about your Salaries.
         */
        use("/api/salaries")
                .get("/", (req, resp) -> {
                    LocalDate periodStart = LocalDate.parse(req.param("periodStart").value("2016-01-01"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEnd = LocalDate.parse(req.param("periodEnd").value("2016-12-31"), DateTimeFormat.forPattern("yyyy-MM-dd"));

                    //final Timer timer = metricRegistry.timer(name("salary", "response"));
                    //final Timer.Context context = timer.time();
                    try {
                        DataSource db = req.require(DataSource.class);
                        resp.send(new SalaryService(db).usersalarypermonthbyyear(periodStart, periodEnd));
                    } finally {
                        //context.stop();
                    }
                }).attr("role", "tm.admin")

                .get("/search/findActiveByDate", (req, resp) -> {
                    LocalDate date = LocalDate.parse(req.param("date").value(), DateTimeFormat.forPattern("yyyy-MM-dd"));

                    //final Timer timer = metricRegistry.timer(name("salary", "search", "findActiveByDate", "response"));
                    //final Timer.Context context = timer.time();
                    try {
                        DataSource db = req.require(DataSource.class);
                        resp.send(new SalaryService(db).findActiveByDate(date));
                    } finally {
                        //context.stop();
                    }
                }).attr("role", "tm.admin")
                .produces("json")
                .consumes("json");


        /**
         * Everything about the capacity
         */
        use("/api/capacities")
                .get("/", (req, resp) -> {
                    LocalDate periodStart = LocalDate.parse(req.param("periodStart").value("2016-01-01"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEnd = LocalDate.parse(req.param("periodEnd").value("2016-12-31"), DateTimeFormat.forPattern("yyyy-MM-dd"));

                    //final Timer timer = metricRegistry.timer(name("capacity", "response"));
                    //final Timer.Context context = timer.time();
                    try {
                        DataSource db = req.require(DataSource.class);
                        resp.send(new UserService(db).capacitypermonth(periodStart, periodEnd));
                    } finally {
                        //context.stop();
                    }
                }).attr("role", "tm.admin")
                .produces("json")
                .consumes("json");

        use("/api/availabilities")
                .get("/", (req, resp) -> {
                    LocalDate periodStart = LocalDate.parse(req.param("periodStart").value("2016-01-01"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEnd = LocalDate.parse(req.param("periodEnd").value("2016-12-31"), DateTimeFormat.forPattern("yyyy-MM-dd"));

                    //final Timer timer = metricRegistry.timer(name("availability", "response"));
                    //final Timer.Context context = timer.time();
                    try {
                        DataSource db = req.require(DataSource.class);
                        resp.send(new UserService(db).useravailabilitypermonthbyyear(periodStart, periodEnd));
                    } finally {
                        //context.stop();
                    }
                }).attr("role", "tm.user")
                .produces("json")
                .consumes("json");
/*
        use("/servlets/metrics")
                .get("/", (req, resp) -> {
                    resp.send(metricsMapper.valueToTree(metricRegistry));
                });
*/

        use(new Metrics()
                .request()
                .threadDump()
                .ping()
                .metric("memory", new MemoryUsageGaugeSet())
                .metric("threads", new ThreadStatesGaugeSet())
                .metric("gc", new GarbageCollectorMetricSet())
                .metric("fs", new FileDescriptorRatioGauge())
        );

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
