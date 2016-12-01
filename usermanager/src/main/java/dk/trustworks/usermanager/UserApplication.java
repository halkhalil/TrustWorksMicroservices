package dk.trustworks.usermanager;

import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import dk.trustworks.framework.security.JwtModule;
import dk.trustworks.framework.security.JwtToken;
import dk.trustworks.usermanager.dto.User;
import dk.trustworks.usermanager.service.SalaryService;
import dk.trustworks.usermanager.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.jooby.Jooby;
import org.jooby.RequestLogger;
import org.jooby.Result;
import org.jooby.Results;
import org.jooby.jdbc.Jdbc;
import org.jooby.json.Jackson;
import org.jooby.metrics.Metrics;
import org.jooby.raml.Raml;
import org.jooby.swagger.SwaggerUI;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by hans on 16/03/15.
 */
public class UserApplication extends Jooby {

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
        //new Raml().install(this);
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

        use("/jwttoken")
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
                            .signWith(SignatureAlgorithm.HS512, JwtModule.KEY)
                            .compact();
                    resp.send(new JwtToken(compactJws));
                })
                .get("/:key", (req, resp) -> {
                    String jwtToken = req.param("key").value();
                    Jws<Claims> claims = Jwts.parser()
                            .setSigningKey(JwtModule.KEY)
                            .parseClaimsJws(jwtToken);
                    resp.send(claims.getBody());
                })
                .produces("json")
                .consumes("json");

        on("dev", () -> use(new JwtModule(true)))
                .orElse(() -> use(new JwtModule(true)));

        use("/api/users")
                /**
                 * @ApiImplicitParams({ @ApiImplicitParam(name = "methodArgumentName",
                 * value = "Some info.",
                 * dataType = "string",
                 * paramType = "header") })
                 */
                .get("/", (req, resp) -> {
                    DataSource db = req.require(DataSource.class);
                    List<User> users = UserService.getInstance(db).findAll();
                    resp.send(users);
                })

                .get("/:uuid", (req, resp) -> {
                    String uuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(UserService.getInstance(db).findByUUID(uuid));
                })

                .get("/{uuid}/capacities", (req, resp) -> {
                    LocalDate periodStart = LocalDate.parse(req.param("periodStart").value("2016-01-01"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEnd = LocalDate.parse(req.param("periodEnd").value("2016-12-31"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    String userUUID = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(UserService.getInstance(db).capacitypermonthbyuser(userUUID, periodStart, periodEnd));
                })

                .get("/{uuid}/availabilities", (req, resp) -> {
                    LocalDate periodStart = LocalDate.parse(req.param("periodStart").value("2016-01-01"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEnd = LocalDate.parse(req.param("periodEnd").value("2016-12-31"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    String userUUID = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(UserService.getInstance(db).useravailabilitypermonthbyyearbyuser(userUUID, periodStart, periodEnd));
                })

                .get("/{uuid}/salaries", (req, resp) -> {
                    LocalDate periodStart = LocalDate.parse(req.param("periodStart").value("2016-01-01"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEnd = LocalDate.parse(req.param("periodEnd").value("2016-12-31"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    String userUUID = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(SalaryService.getInstance(db).usersalarypermonthbyyearbyuser(userUUID, periodStart, periodEnd));
                })

                // Verified
                .get("/search/findByActiveTrue", (req, resp) -> {
                    DataSource db = req.require(DataSource.class);
                    resp.send(UserService.getInstance(db).findByActiveTrue());
                })


                .get("/search/findByUsername", (req, resp) -> {
                    String username = req.param("username").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(UserService.getInstance(db).findByUsername(username));
                })

                .get("/search/findByUsernameAndPasswordAndActiveTrue", (req, resp) -> {
                    String username = req.param("username").value();
                    String password = req.param("password").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(UserService.getInstance(db).findByUsernameAndPasswordAndActiveTrue(username, password));

                })

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
                    DataSource db = req.require(DataSource.class);
                    resp.send(SalaryService.getInstance(db).usersalarypermonthbyyear(periodStart, periodEnd));
                })

                .get("/search/findActiveByDate", (req, resp) -> {
                    LocalDate date = LocalDate.parse(req.param("date").value(), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    DataSource db = req.require(DataSource.class);
                    resp.send(SalaryService.getInstance(db).findActiveByDate(date));
                })
                .produces("json")
                .consumes("json");


        /**
         * Hourly capacity in TrustWorks by month
         */
        use("/api/capacities")
                .get("/", (req, resp) -> {
                    LocalDate periodStart = LocalDate.parse(req.param("periodStart").value("2016-01-01"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEnd = LocalDate.parse(req.param("periodEnd").value("2016-12-31"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    DataSource db = req.require(DataSource.class);
                    resp.send(UserService.getInstance(db).capacitypermonth(periodStart, periodEnd));
                })
                .produces("json")
                .consumes("json");

        use("/api/availabilities")
                .get("/", (req, resp) -> {
                    LocalDate periodStart = LocalDate.parse(req.param("periodStart").value("2016-01-01"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEnd = LocalDate.parse(req.param("periodEnd").value("2016-12-31"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    DataSource db = req.require(DataSource.class);
                    resp.send(UserService.getInstance(db).useravailabilitypermonthbyyear(periodStart, periodEnd));
                })
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

        new SwaggerUI().install(this);


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
