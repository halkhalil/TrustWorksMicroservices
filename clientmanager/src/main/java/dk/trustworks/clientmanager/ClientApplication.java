package dk.trustworks.clientmanager;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import dk.trustworks.clientmanager.service.*;
import dk.trustworks.framework.model.*;
import dk.trustworks.framework.security.JwtModule;
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
import org.jooby.swagger.SwaggerUI;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;

/**
 * Created by hans on 16/03/15.
 */
public class ClientApplication extends Jooby {
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

        try {
            registerInZookeeper("clientservice", System.getProperty("zookeeper.host"), System.getProperty("application.host"), Integer.parseInt(System.getProperty("application.port")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        use(new Jdbc());
        use(new Jackson().module(new JodaModule()));

        get("/", () -> HOME);

        on("dev", () -> use(new JwtModule(false)))
                .orElse(() -> use(new JwtModule(true)));

        use("/api/clients")
                .get("/", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    DataSource db = req.require(DataSource.class);
                    resp.send(ClientService.getInstance(db).findAll(projection));
                })

                .get("/:uuid", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    String uuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(ClientService.getInstance(db).findByUUID(uuid, projection));
                })

                .get("/search/findByActiveTrue", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    DataSource db = req.require(DataSource.class);
                    resp.send(ClientService.getInstance(db).findByActiveTrue(projection));
                })

                .post("/", (req, resp) -> {
                    Client client = req.body(Client.class);
                    DataSource db = req.require(DataSource.class);
                    ClientService.getInstance(db).create(client);
                    resp.send(Status.CREATED);
                })

                .put("/:uuid", (req, resp) -> {
                    String uuid = req.param("uuid").value();
                    Client client = req.body(Client.class);
                    DataSource db = req.require(DataSource.class);
                    ClientService.getInstance(db).update(client, uuid);
                    resp.send(Status.OK);
                })

                .get("/:uuid/projects", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    String clientuuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(ProjectService.getInstance(db).findByClientUUID(clientuuid, projection));
                })

                .get("/:uuid/projects/search/findByActiveTrue", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    String clientuuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(ProjectService.getInstance(db).findByClientUUIDAndActiveTrue(clientuuid, projection));
                })
                .produces("json")
                .consumes("json");

        use("/api/projects")
                .get("/", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    DataSource db = req.require(DataSource.class);
                    resp.send(ProjectService.getInstance(db).findAll(projection));
                })

                .get("/:uuid", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    String uuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(ProjectService.getInstance(db).findByUUID(uuid, projection));
                })

                .get("/:uuid/tasks", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    String projectuuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(TaskService.getInstance(db).findByProjectUUID(projectuuid, projection));
                })

                .get("/:uuid/budget", (req, resp) -> {
                    String projectuuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(ProjectService.getInstance(db).getProjectBudget(projectuuid));
                })

                .get("/:uuid/revenues/days", (req, resp) -> {
                    String periodStart = req.param("periodStart").value("2016-01-01");
                    String periodEnd = req.param("periodEnd").value("2016-01-31");
                    String projectUUID = req.param("uuid").value();

                    LocalDate periodStartDate = LocalDate.parse(periodStart, DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEndDate = LocalDate.parse(periodEnd, DateTimeFormat.forPattern("yyyy-MM-dd"));
                    DataSource db = req.require(DataSource.class);
                    resp.send(RevenueService.getInstance(db).revenuePerDayPerProject(periodStartDate, periodEndDate, projectUUID));
                })

                .get("/:uuid/revenues/months", (req, resp) -> {
                    String periodStart = req.param("periodStart").value("2016-01-01");
                    String periodEnd = req.param("periodEnd").value("2016-06-31");
                    String projectUUID = req.param("uuid").value();

                    LocalDate periodStartDate = LocalDate.parse(periodStart, DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEndDate = LocalDate.parse(periodEnd, DateTimeFormat.forPattern("yyyy-MM-dd"));
                    DataSource db = req.require(DataSource.class);
                    resp.send(RevenueService.getInstance(db).revenuePerMonthPerProject(periodStartDate, periodEndDate, projectUUID));
                })

                .get("/:uuid/revenues/years", (req, resp) -> {
                    String periodStart = req.param("periodStart").value("2014-01-01");
                    String periodEnd = req.param("periodEnd").value("2016-01-31");
                    String projectUUID = req.param("uuid").value();

                    LocalDate periodStartDate = LocalDate.parse(periodStart, DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEndDate = LocalDate.parse(periodEnd, DateTimeFormat.forPattern("yyyy-MM-dd"));
                    DataSource db = req.require(DataSource.class);
                    resp.send(RevenueService.getInstance(db).revenuePerYearPerProject(periodStartDate, periodEndDate, projectUUID));
                })

                .get("/search/findByActiveTrue", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    DataSource db = req.require(DataSource.class);
                    resp.send(ProjectService.getInstance(db).findByActiveTrue(projection));
                })

                .get("/search/findByClientUUID", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    String clientUUID = req.param("clientuuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(ProjectService.getInstance(db).findByClientUUID(clientUUID, projection));
                })

                .get("/search/findByClientUUIDAndActiveTrue", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    String clientUUID = req.param("clientuuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(ProjectService.getInstance(db).findByClientUUIDAndActiveTrue(clientUUID, projection));
                })

                .post("/", (req, resp) -> {
                    Project project = req.body(Project.class);
                    DataSource db = req.require(DataSource.class);
                    ProjectService.getInstance(db).create(project);
                    resp.send(Status.CREATED);
                })

                .put("/:uuid", (req, resp) -> {
                    String uuid = req.param("uuid").value();
                    Project project = req.body(Project.class);
                    DataSource db = req.require(DataSource.class);
                    ProjectService.getInstance(db).update(project, uuid);
                    resp.send(Status.OK);
                })

                .produces("json")
                .consumes("json");

        use("/api/tasks")
                .get("/", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    DataSource db = req.require(DataSource.class);
                    resp.send(TaskService.getInstance(db).findAll(projection));
                })

                .get("/:uuid", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    String uuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(TaskService.getInstance(db).findByUUID(uuid, projection));
                })

                .get("/:uuid/taskuserprice", (req, resp) -> {
                    String uuid = req.param("uuid").value();
                    String projection = req.param("projection").value("");
                    DataSource db = req.require(DataSource.class);
                    resp.send(TaskWorkerConstraintService.getInstance(db).findByTaskUUID(uuid, projection));
                })

                .get("/search/findByProjectUUID", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    String projectUUID = req.param("projectuuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(TaskService.getInstance(db).findByProjectUUID(projectUUID, projection));
                })

                .post("/", (req, resp) -> {
                    Task task = req.body(Task.class);
                    DataSource db = req.require(DataSource.class);
                    TaskService.getInstance(db).create(task);
                    resp.send(Status.CREATED);
                })

                .put("/:uuid", (req, resp) -> {
                    String uuid = req.param("uuid").value();
                    Task task = req.body(Task.class);
                    DataSource db = req.require(DataSource.class);
                    TaskService.getInstance(db).update(task, uuid);
                    resp.send(Status.OK);
                })

                .produces("json")
                .consumes("json");

        use("/api/taskuserprice")
                .get("/", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    DataSource db = req.require(DataSource.class);
                    resp.send(TaskWorkerConstraintService.getInstance(db).findAll(projection));
                })

                .get("/:uuid", (req, resp) -> {
                    String uuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(TaskWorkerConstraintService.getInstance(db).findByUUID(uuid));
                })

                .get("/search/findByProjectUUID", (req, resp) -> {
                    String taskUUID = req.param("taskuuid").value();
                    String projection = req.param("projection").value("");
                    DataSource db = req.require(DataSource.class);
                    resp.send(TaskWorkerConstraintService.getInstance(db).findByTaskUUID(taskUUID, projection));
                })

                .get("/search/findByTaskUUIDAndUserUUID", (req, resp) -> {
                    String taskUUID = req.param("taskuuid").value();
                    String userUUID = req.param("useruuid").value();
                    String projection = req.param("projection").value("");
                    DataSource db = req.require(DataSource.class);
                    resp.send(TaskWorkerConstraintService.getInstance(db).findByTaskUUIDAndUserUUID(taskUUID, userUUID, projection));
                })

                .post("/", (req, resp) -> {
                    TaskWorkerConstraint taskWorkerConstraint = req.body(TaskWorkerConstraint.class);
                    DataSource db = req.require(DataSource.class);
                    TaskWorkerConstraintService.getInstance(db).create(taskWorkerConstraint);
                    resp.send(Status.CREATED);
                })

                .put("/:uuid", (req, resp) -> {
                    String uuid = req.param("uuid").value();
                    TaskWorkerConstraint taskWorkerConstraint = req.body(TaskWorkerConstraint.class);
                    DataSource db = req.require(DataSource.class);
                    TaskWorkerConstraintService.getInstance(db).update(taskWorkerConstraint, uuid);
                    resp.send(Status.OK);
                })

                .produces("json")
                .consumes("json");

        use("/api/budget")
                .get("/", (req, resp) -> {
                    DataSource db = req.require(DataSource.class);
                    TaskWorkerConstraintBudgetService.getInstance(db).addUserTask();
                    resp.send(Status.OK);
                })

                .get("/search/findByPeriod", (req, resp) -> {
                    LocalDate fromPeriod = LocalDate.parse(req.param("periodStart").value("2016-01-01"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate toPeriod = LocalDate.parse(req.param("periodEnd").value("2016-12-31"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    int ahead = req.param("ahead").intValue(0);
                    DataSource db = req.require(DataSource.class);
                    List<TaskWorkerConstraintBudget> budgets = TaskWorkerConstraintBudgetService.getInstance(db).findByPeriod(fromPeriod, toPeriod, ahead);
                    resp.send(budgets);
                })

                .get("/search/findByTaskUUIDAndUserUUID", (req, resp) -> {
                    String userUUID = req.param("useruuid").value();
                    String taskUUID = req.param("taskuuid").value();
                    DataSource db = req.require(DataSource.class);
                    List<TaskWorkerConstraintBudget> budgets = TaskWorkerConstraintBudgetService.getInstance(db).findByTaskUUIDAndUserUUID(userUUID, taskUUID);
                    resp.send(budgets);
                })

                .post("/", (req, resp) -> {
                    TaskWorkerConstraintBudget budget = req.body(TaskWorkerConstraintBudget.class);
                    DataSource db = req.require(DataSource.class);
                    TaskWorkerConstraintBudgetService.getInstance(db).create(budget);
                    resp.send(Status.OK);
                })

                .produces("json")
                .consumes("json");

        use("/api/clientdatas")
                .get("/", (req, resp) -> {
                    DataSource db = req.require(DataSource.class);
                    List<ClientData> clientDatas = ClientDataService.getInstance(db).findAll();
                    resp.send(clientDatas);
                })

                .get("/:uuid", (req, resp) -> {
                    String uuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    ClientData clientData = ClientDataService.getInstance(db).findByUUID(uuid);
                    resp.send(clientData);
                })

                .post("/", (req, resp) -> {
                    ClientData clientData = req.body(ClientData.class);
                    DataSource db = req.require(DataSource.class);
                    resp.send(ClientDataService.getInstance(db).create(clientData));
                })

                .put("/:uuid", (req, resp) -> {
                    String uuid = req.param("uuid").value();
                    ClientData clientData = req.body(ClientData.class);
                    DataSource db = req.require(DataSource.class);
                    ClientDataService.getInstance(db).update(clientData, uuid);
                    resp.send(Status.OK);
                })

                .produces("json")
                .consumes("json");

        use("/api/revenues")
                .get("/", (req, resp) -> {
                    resp.send(new Err(403));
                })

                .get("/users", (req, resp) -> {
                    String periodStart = req.param("periodStart").value("2016-01-01");
                    String periodEnd = req.param("periodEnd").value("2016-01-31");
                    LocalDate periodStartDate = LocalDate.parse(periodStart, DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEndDate = LocalDate.parse(periodEnd, DateTimeFormat.forPattern("yyyy-MM-dd"));

                    DataSource db = req.require(DataSource.class);
                    Collection<Revenue> revenues = RevenueService.getInstance(db).revenuePerUser(periodStartDate, periodEndDate);
                    resp.send(revenues);
                })

                .get("/projects", (req, resp) -> {
                    String periodStart = req.param("periodStart").value("2016-01-01");
                    String periodEnd = req.param("periodEnd").value("2016-01-31");
                    LocalDate periodStartDate = LocalDate.parse(periodStart, DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEndDate = LocalDate.parse(periodEnd, DateTimeFormat.forPattern("yyyy-MM-dd"));

                    DataSource db = req.require(DataSource.class);
                    Collection<Revenue> revenues = RevenueService.getInstance(db).revenuePerProject(periodStartDate, periodEndDate);
                    resp.send(revenues);
                })

                /**
                 * Return the total revenue per day.
                 * @param periodStart The starting date for the calculation. <br />This day is included in the list.
                 * @param periodEnd The ending date for the calculation. <br />This day is included in the list.
                 * @requireToken JSON Token
                 */
                .get("/days", (req, resp) -> {
                    String periodStart = req.param("periodStart").value("2016-01-01");
                    String periodEnd = req.param("periodEnd").value("2016-01-31");
                    LocalDate periodStartDate = LocalDate.parse(periodStart, DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEndDate = LocalDate.parse(periodEnd, DateTimeFormat.forPattern("yyyy-MM-dd"));

                    DataSource db = req.require(DataSource.class);
                    Collection<Revenue> revenues = RevenueService.getInstance(db).revenuePerDay(periodStartDate, periodEndDate);
                    resp.send(revenues);
                })

                /**
                 * Return the total revenue per month.
                 * @param periodStart The starting date for the calculation. <br />This date is reset to the first date of the month. <br />This month is included in the list.
                 * @param periodEnd The ending date for the calculation. <br />This date is reset to the first date of the month. <br />This month is included in the list.
                 * @requiretoken
                 */
                .get("/months", (req, resp) -> {
                    String periodStart = req.param("periodStart").value("2016-01-01");
                    String periodEnd = req.param("periodEnd").value("2016-12-31");
                    LocalDate periodStartDate = LocalDate.parse(periodStart, DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEndDate = LocalDate.parse(periodEnd, DateTimeFormat.forPattern("yyyy-MM-dd"));
                    periodStartDate = periodStartDate.withDayOfMonth(periodEndDate.dayOfMonth().getMinimumValue());
                    periodEndDate = periodEndDate.withDayOfMonth(periodEndDate.dayOfMonth().getMaximumValue());

                    DataSource db = req.require(DataSource.class);
                    Collection<Revenue> revenues = RevenueService.getInstance(db).revenuePerMonth(periodStartDate, periodEndDate);
                    resp.send(revenues);
                })

                /**
                 * Return the total revenue per year.
                 * @param periodStart The starting date for the calculation. <br />This date is reset to the first date of the year. <br />This month is included in the list.
                 * @param periodEnd The ending date for the calculation. <br />This date is reset to the first date of the year. <br />This month is included in the list.
                 * @requiretoken
                 */
                .get("/years", (req, resp) -> {
                    String periodStart = req.param("periodStart").value("2014-01-01");
                    String periodEnd = req.param("periodEnd").value("2016-01-01");

                    LocalDate periodStartDate = LocalDate.parse(periodStart, DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEndDate = LocalDate.parse(periodEnd, DateTimeFormat.forPattern("yyyy-MM-dd"));

                    periodStartDate = periodStartDate.withDayOfYear(periodStartDate.dayOfYear().getMinimumValue());
                    periodEndDate = periodEndDate.withDayOfYear(periodEndDate.dayOfYear().getMaximumValue());

                    DataSource db = req.require(DataSource.class);
                    Collection<Revenue> revenues = RevenueService.getInstance(db).revenuePerYear(periodStartDate, periodEndDate);
                    resp.send(revenues);
                })

                .produces("json")
                .consumes("json");

        new SwaggerUI().install(this);
    }

    public static void main(final String[] args) throws Throwable {
        new ClientApplication().start();
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
}
