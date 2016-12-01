package dk.trustworks.timemanager;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import dk.trustworks.framework.security.JwtModule;
import dk.trustworks.timemanager.dto.WeekItem;
import dk.trustworks.timemanager.dto.Work;
import dk.trustworks.timemanager.service.ReportService;
import dk.trustworks.timemanager.service.WeekItemService;
import dk.trustworks.timemanager.service.WeekService;
import dk.trustworks.timemanager.service.WorkService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.joda.time.LocalDate;
import org.jooby.*;
import org.jooby.jdbc.Jdbc;
import org.jooby.json.Jackson;
import org.jooby.raml.Raml;
import org.jooby.swagger.SwaggerUI;

import javax.sql.DataSource;

/**
 * Created by hans on 16/03/15.
 */
public class TimeApplication  extends Jooby {// extends BaseApplication {

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
            registerInZookeeper("timeservice", System.getProperty("zookeeper.host"), System.getProperty("application.host"), Integer.parseInt(System.getProperty("application.port")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        use(new Jdbc());
        use(new Jackson().module(new JodaModule()));

        get("/", () -> HOME);

        on("dev", () -> use(new JwtModule(true)))
                .orElse(() -> use(new JwtModule(true)));

        use("/api/weekitems")
                .get("/", (req, resp) -> {
                    DataSource db = req.require(DataSource.class);
                    resp.send(WeekItemService.getInstance(db).findAll());
                })

                .get("/:uuid", (req, resp) -> {
                    String uuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(WeekItemService.getInstance(db).findByUUID(uuid));
                })

                .get("/search/findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc", (req, resp) -> {
                    int weekNumber = req.param("weeknumber").intValue();
                    int year = req.param("year").intValue();
                    String userUUID = req.param("useruuid").value();

                    DataSource db = req.require(DataSource.class);
                    resp.send(WeekItemService.getInstance(db).findByWeekNumberAndYearAndUserUUIDOrderBySortingAsc(weekNumber, year, userUUID));
                })

                .get("/search/findByWeekNumberAndYearAndUserUUIDAndTaskUUIDOrderBySortingAsc", (req, resp) -> {
                    int weekNumber = req.param("weeknumber").intValue();
                    int year = req.param("year").intValue();
                    String userUUID = req.param("useruuid").value();
                    String taskUUID = req.param("taskuuid").value();

                    DataSource db = req.require(DataSource.class);
                    resp.send(WeekItemService.getInstance(db).findByWeekNumberAndYearAndUserUUIDAndTaskUUIDOrderBySortingAsc(weekNumber, year, userUUID, taskUUID));
                })

                .post("/", (req, resp) -> {
                    WeekItem weekItem = req.body(WeekItem.class);
                    DataSource db = req.require(DataSource.class);
                    WeekItemService.getInstance(db).create(weekItem);
                    resp.send(Status.OK);
                })

                .post("/commands/cloneweek", (req, resp) -> {
                    int weekNumber = req.param("weeknumber").intValue();
                    int year = req.param("year").intValue();
                    String userUUID = req.param("useruuid").value();

                    DataSource db = req.require(DataSource.class);
                    resp.send(WeekItemService.getInstance(db).cloneWeek(weekNumber, year, userUUID));
                })

                .produces("json")
                .consumes("json");

        use("/api/works")
                .get("/", (req, resp) -> {
                    throw new Err(416);
                })

                .get("/:uuid", (req, resp) -> {
                    throw new Err(416);
                })

                .get("/search/findByTaskUUID", (req, resp) -> {
                    String taskuuid = req.param("taskuuid").value();

                    DataSource db = req.require(DataSource.class);
                    resp.send(WorkService.getInstance(db).findByTaskUUID(taskuuid));
                })

                .get("/search/findByProjectUUID", (req, resp) -> {
                    String projectUUID = req.param("projectuuid").value();

                    DataSource db = req.require(DataSource.class);
                    resp.send(WorkService.getInstance(db).findByProjectUUID(projectUUID));
                })

                .get("/search/findByYear", (req, resp) -> {
                    int year = req.param("year").intValue();

                    DataSource db = req.require(DataSource.class);
                    resp.send(WorkService.getInstance(db).findByYear(year));
                })

                .get("/search/findByYearAndUserUUID", (req, resp) -> {
                    int year = req.param("year").intValue();
                    String useruuid = req.param("useruuid").value();

                    DataSource db = req.require(DataSource.class);
                    resp.send(WorkService.getInstance(db).findByYearAndUserUUID(useruuid, year));
                })

                .get("/search/findByYearAndMonth", (req, resp) -> {
                    int year = req.param("year").intValue();
                    int month = req.param("month").intValue();

                    DataSource db = req.require(DataSource.class);
                    resp.send(WorkService.getInstance(db).findByYearAndMonth(year, month));
                })

                .get("/search/findByYearAndMonthAndTaskUUIDAndUserUUID", (req, resp) -> {
                    int year = req.param("year").intValue();
                    int month = req.param("month").intValue();
                    String taskuuid = req.param("taskuuid").value();
                    String useruuid = req.param("useruuid").value();

                    DataSource db = req.require(DataSource.class);
                    resp.send(WorkService.getInstance(db).findByYearAndMonthAndTaskUUIDAndUserUUID(year, month, taskuuid, useruuid));
                })

                .get("/search/findByYearAndMonthAndDayAndTaskUUIDAndUserUUID", (req, resp) -> {
                    int year = req.param("year").intValue();
                    int month = req.param("month").intValue();
                    int day = req.param("day").intValue();
                    String taskuuid = req.param("taskuuid").value();
                    String useruuid = req.param("useruuid").value();

                    DataSource db = req.require(DataSource.class);
                    resp.send(WorkService.getInstance(db).findByYearAndMonthAndDayAndTaskUUIDAndUserUUID(year, month, day, taskuuid, useruuid));
                })

                .get("/search/findByYearAndMonthAndDay", (req, resp) -> {
                    int year = req.param("year").intValue();
                    int month = req.param("month").intValue();
                    int day = req.param("day").intValue();

                    DataSource db = req.require(DataSource.class);
                    resp.send(WorkService.getInstance(db).findByYearAndMonthAndDay(year, month, day));
                })

                .get("/search/findByYearAndMonthAndTaskUUID", (req, resp) -> {
                    int year = req.param("year").intValue();
                    int month = req.param("month").intValue();
                    String taskuuid = req.param("taskuuid").value();

                    DataSource db = req.require(DataSource.class);
                    resp.send(WorkService.getInstance(db).findByYearAndMonthAndTaskUUID(year, month, taskuuid));
                }).attr("role", "tm.user")

                .get("/search/findByYearAndTaskUUIDAndUserUUID", (req, resp) -> {
                    int year = req.param("year").intValue();
                    String taskuuid = req.param("taskuuid").value();
                    String useruuid = req.param("useruuid").value();

                    DataSource db = req.require(DataSource.class);
                    resp.send(WorkService.getInstance(db).findByYearAndTaskUUIDAndUserUUID(year, taskuuid, useruuid));
                })

                .get("/sums", (req, resp) -> {
                    String taskuuid = req.param("taskuuid").value();
                    String useruuid = req.param("useruuid").value();

                    DataSource db = req.require(DataSource.class);
                    resp.send(WorkService.getInstance(db).calculateTaskUserTotalDuration(taskuuid, useruuid));
                })

                // Verified
                .post("/", (req, resp) -> {
                    Work work = req.body(Work.class);
                    DataSource db = req.require(DataSource.class);
                    WorkService.getInstance(db).create(work);
                    resp.send(Status.OK);
                })

                .produces("json")
                .consumes("json");


        use("/api/weeks")
            .get("/", (req, resp) -> {
                throw new Err(416);
            }).attr("role", "tm.user")

            .get("/:uuid", (req, resp) -> {
                throw new Err(416);
            }).attr("role", "tm.user")

            .get("/search/findByWeekNumberAndYearAndUserUUID", (req, resp) -> {
                int weeknumber = req.param("weeknumber").intValue();
                int year = req.param("year").intValue();
                String userUUID = req.param("useruuid").value();

                DataSource db = req.require(DataSource.class);
                resp.send(WeekService.getInstance(db).findByWeekNumberAndYearAndUserUUID(weeknumber, year, userUUID));
            })

            .produces("json")
            .consumes("json");

        use("/api/reports")
                .get("/", (req, resp) -> {
                    throw new Err(416);
                })

                .get("/:uuid", (req, resp) -> {
                    throw new Err(416);
                })

                .get("/search/findByYearAndMonth", (req, resp) -> {
                    int year = req.param("year").intValue(LocalDate.now().getYear());
                    int month = req.param("month").intValue(LocalDate.now().getMonthOfYear()-1);

                    DataSource db = req.require(DataSource.class);
                    resp.send(ReportService.getInstance(db).findByYearAndMonth(year, month));
                })

                .produces("json")
                .consumes("json");

        new SwaggerUI().install(this);
    }

    public static void main(final String[] args) throws Throwable {
        new TimeApplication().start();
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


    /*
    public static void main(String[] args) throws Exception {
        new TimeApplication();
    }
*/

    /*
    public TimeApplication() throws Exception {
        DeploymentManager manager = getMetricsDeploymentManager();

        ServiceRegistry serviceRegistry = ServiceRegistry.getInstance();

        serviceRegistry.registerService("taskuuid", new TaskService());
        serviceRegistry.registerService("useruuid", new UserService());
        serviceRegistry.registerService("projectuuid", new ProjectService());
        serviceRegistry.registerService("clientuuid", new ClientService());

        Undertow.builder()
                .addHttpListener(Integer.parseInt(System.getProperty("application.port")), System.getProperty("application.host"))
                .setBufferSize(1024 * 16)
                .setIoThreads(Runtime.getRuntime().availableProcessors() * 2) //this seems slightly faster in some configurations
                .setSocketOption(Options.BACKLOG, 10000)
                .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false) //don't send a keep-alive header for HTTP/1.1 requests, as it is not required
                .setServerOption(UndertowOptions.ALWAYS_SET_DATE, true)
                .setHandler(Handlers.header(Handlers.path()
                                .addPrefixPath("/api/works", new WorkHandler())
                                .addPrefixPath("/api/weeks", new WeekHandler())
                                .addPrefixPath("/api/taskweekviews/", new TaskWeekViewHandler())
                                .addPrefixPath("/servlets", manager.start())
                        , Headers.SERVER_STRING, "U-tow"))
                .setWorkerThreads(200)
                .build()
                .start();

        registerInZookeeper("timeservice", System.getProperty("zookeeper.host"), System.getProperty("application.host"), Integer.parseInt(System.getProperty("application.port")));
    }*/


}
