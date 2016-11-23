package dk.trustworks.clientmanager;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import dk.trustworks.clientmanager.model.Client;
import dk.trustworks.clientmanager.model.ClientData;
import dk.trustworks.clientmanager.model.TaskWorkerConstraintBudget;
import dk.trustworks.clientmanager.service.*;
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

import javax.sql.DataSource;
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
                    JwtModule.authorize(req);
                    DataSource db = req.require(DataSource.class);
                    resp.send(new ClientService(db).findAll(projection));
                }).attr("role", "tm.user")

                .get("/:uuid", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    JwtModule.authorize(req);
                    String uuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(new ClientService(db).findByUUID(uuid, projection));
                }).attr("role", "tm.user")

                .get("/search/findByActiveTrue", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    JwtModule.authorize(req);
                    DataSource db = req.require(DataSource.class);
                    resp.send(new ClientService(db).findByActiveTrue(projection));
                }).attr("role", "tm.user")

                .post("/", (req, resp) -> {
                    Client client = req.body(Client.class);
                    JwtModule.authorize(req);

                    DataSource db = req.require(DataSource.class);
                    new ClientService(db).create(client);
                    resp.send(Status.CREATED);
                }).attr("role", "tm.editor")

                .put("/:uuid", (req, resp) -> {
                    String uuid = req.param("uuid").value();
                    Client client = req.body(Client.class);
                    JwtModule.authorize(req);

                    DataSource db = req.require(DataSource.class);
                    new ClientService(db).update(client, uuid);
                    resp.send(Status.OK);
                }).attr("role", "tm.editor")

                .get("/:uuid/projects", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    JwtModule.authorize(req);
                    String clientuuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(new ProjectService(db).findByClientUUID(clientuuid, projection));
                }).attr("role", "tm.user")

                .get("/:uuid/projects/search/findByActiveTrue", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    JwtModule.authorize(req);
                    String clientuuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(new ProjectService(db).findByClientUUIDAndActiveTrue(clientuuid, projection));
                }).attr("role", "tm.user")

                .produces("json")
                .consumes("json");

        use("/api/projects")
                .get("/", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    JwtModule.authorize(req);
                    DataSource db = req.require(DataSource.class);
                    resp.send(new ProjectService(db).findAll(projection));
                }).attr("role", "tm.user")

                .get("/:uuid", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    String uuid = req.param("uuid").value();
                    JwtModule.authorize(req);
                    DataSource db = req.require(DataSource.class);
                    resp.send(new ProjectService(db).findByUUID(uuid, projection));
                }).attr("role", "tm.user")

                .get("/:uuid/tasks", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    JwtModule.authorize(req);
                    String projectuuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(new TaskService(db).findByProjectUUID(projectuuid, projection));
                }).attr("role", "tm.user")

                .get("/:uuid/budget", (req, resp) -> {
                    JwtModule.authorize(req);
                    String projectuuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(new ProjectService(db).getProjectBudget(projectuuid));
                }).attr("role", "tm.user")

                .get("/search/findByActiveTrue", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    JwtModule.authorize(req);
                    DataSource db = req.require(DataSource.class);
                    resp.send(new ProjectService(db).findByActiveTrue(projection));
                }).attr("role", "tm.user")

                .get("/search/findByClientUUID", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    String clientUUID = req.param("clientuuid").value();
                    JwtModule.authorize(req);
                    DataSource db = req.require(DataSource.class);
                    resp.send(new ProjectService(db).findByClientUUID(clientUUID, projection));
                }).attr("role", "tm.user")

                .get("/search/findByClientUUIDAndActiveTrue", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    String clientUUID = req.param("clientuuid").value();
                    JwtModule.authorize(req);
                    DataSource db = req.require(DataSource.class);
                    resp.send(new ProjectService(db).findByClientUUIDAndActiveTrue(clientUUID, projection));
                }).attr("role", "tm.user")

                .produces("json")
                .consumes("json");

        use("/api/tasks")
                .get("/", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    JwtModule.authorize(req);
                    DataSource db = req.require(DataSource.class);
                    resp.send(new TaskService(db).findAll(projection));
                }).attr("role", "tm.user")

                .get("/:uuid", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    JwtModule.authorize(req);
                    String uuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    resp.send(new TaskService(db).findByUUID(uuid, projection));
                }).attr("role", "tm.user")

                .get("/search/findByProjectUUID", (req, resp) -> {
                    String projection = req.param("projection").value("");
                    String projectUUID = req.param("projectuuid").value();
                    JwtModule.authorize(req);
                    DataSource db = req.require(DataSource.class);
                    resp.send(new TaskService(db).findByProjectUUID(projectUUID, projection));
                }).attr("role", "tm.user")

                .produces("json")
                .consumes("json");

        use("/api/taskuserprice")
                .get("/", (req, resp) -> {
                    JwtModule.authorize(req);
                    DataSource db = req.require(DataSource.class);
                    resp.send(new TaskWorkerConstraintService(db).findAll());
                }).attr("role", "tm.user")

                .get("/:uuid", (req, resp) -> {
                    String uuid = req.param("uuid").value();
                    JwtModule.authorize(req);
                    DataSource db = req.require(DataSource.class);
                    resp.send(new TaskWorkerConstraintService(db).findByUUID(uuid));
                }).attr("role", "tm.user")

                .get("/search/findByProjectUUID", (req, resp) -> {
                    String taskUUID = req.param("taskuuid").value();
                    JwtModule.authorize(req);
                    DataSource db = req.require(DataSource.class);
                    resp.send(new TaskWorkerConstraintService(db).findByTaskUUID(taskUUID));
                }).attr("role", "tm.user")

                .get("/search/findByTaskUUIDAndUserUUID", (req, resp) -> {
                    String taskUUID = req.param("taskuuid").value();
                    String userUUID = req.param("useruuid").value();
                    String projection = req.param("projection").value("");
                    JwtModule.authorize(req);
                    DataSource db = req.require(DataSource.class);
                    resp.send(new TaskWorkerConstraintService(db).findByTaskUUIDAndUserUUID(taskUUID, userUUID, projection));
                }).attr("role", "tm.user")

                .produces("json")
                .consumes("json");


        use("/api/budget")
                .get("/", (req, resp) -> {
                    DataSource db = req.require(DataSource.class);
                    new TaskWorkerConstraintBudgetService(db).addUserTask();
                    resp.send(Status.OK);
                }).attr("role", "tm.user")

                .get("/search/findByPeriod", (req, resp) -> {
                    LocalDate fromPeriod = LocalDate.parse(req.param("periodStart").value("2016-01-01"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate toPeriod = LocalDate.parse(req.param("periodEnd").value("2016-12-31"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    int ahead = req.param("ahead").intValue(0);
                    DataSource db = req.require(DataSource.class);
                    List<TaskWorkerConstraintBudget> budgets = new TaskWorkerConstraintBudgetService(db).findByPeriod(fromPeriod, toPeriod, ahead);
                    resp.send(budgets);
                }).attr("role", "tm.user")

                .get("/search/findByTaskUUIDAndUserUUID", (req, resp) -> {
                    String userUUID = req.param("useruuid").value();
                    String taskUUID = req.param("taskuuid").value();
                    DataSource db = req.require(DataSource.class);
                    List<TaskWorkerConstraintBudget> budgets = new TaskWorkerConstraintBudgetService(db).findByTaskUUIDAndUserUUID(userUUID, taskUUID);
                    resp.send(budgets);
                }).attr("role", "tm.user")

                .produces("json")
                .consumes("json");

        use("/api/clientdatas")
                .get("/", (req, resp) -> {
                    DataSource db = req.require(DataSource.class);
                    List<ClientData> clientDatas = new ClientDataService(db).findAll();
                    resp.send(clientDatas);
                }).attr("role", "tm.user")

                .get("/:uuid", (req, resp) -> {
                    String uuid = req.param("uuid").value();
                    DataSource db = req.require(DataSource.class);
                    ClientData clientData = new ClientDataService(db).findByUUID(uuid);
                    resp.send(clientData);
                }).attr("role", "tm.user")

                .produces("json")
                .consumes("json");
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



    /*
    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
        new ClientApplication();
    }

    public ClientApplication() {
        System.out.println("System.getProperty(\"application.host\") = " + System.getProperty("application.host"));
        System.out.println("System.getProperty(\"application.port\") = " + System.getProperty("application.port"));
        System.out.println("System.getProperty(\"db.url\") = " + System.getProperty("db.url"));
        System.out.println("System.getProperty(\"db.user\") = " + System.getProperty("db.user"));
        System.out.println("System.getProperty(\"db.password\") = " + System.getProperty("db.password"));

        DeploymentManager manager = getMetricsDeploymentManager();

        ServiceRegistry serviceRegistry = ServiceRegistry.getInstance();

        serviceRegistry.registerService("taskworkerconstraintbudgetuuid", new TaskWorkerConstraintBudgetService());
        serviceRegistry.registerService("taskworkerconstraintuuid", new TaskWorkerConstraintService());
        serviceRegistry.registerService("taskuuid", new TaskService());
        serviceRegistry.registerService("projectuuid", new ProjectService());
        serviceRegistry.registerService("clientuuid", new ClientService());
        serviceRegistry.registerService("useruuid", new UserService());

        try {
            Undertow.builder()
                    .addHttpListener(Integer.parseInt(System.getProperty("application.port")), System.getProperty("application.host"))
                    .setBufferSize(1024 * 16)
                    .setIoThreads(Runtime.getRuntime().availableProcessors() * 2) //this seems slightly faster in some configurations
                    .setSocketOption(Options.BACKLOG, 10000)
                    .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false) //don't send a keep-alive header for HTTP/1.1 requests, as it is not required
                    .setServerOption(UndertowOptions.ALWAYS_SET_DATE, true)
                    .setHandler(Handlers.header(Handlers.path()
                                    .addPrefixPath("/api/clients", new ClientHandler())
                                    .addPrefixPath("/api/clientdatas", new ClientDataHandler())
                                    .addPrefixPath("/api/projects", new ProjectHandler())
                                    .addPrefixPath("/api/tasks", new TaskHandler())
                                    .addPrefixPath("/api/taskworkerconstraints", new TaskWorkerConstraintHandler())
                                    .addPrefixPath("/api/taskworkerconstraintbudgets", new TaskWorkerConstraintBudgetHandler())
                                    .addPrefixPath("/api/projectbudgets", new ProjectBudgetHandler())
                                    .addPrefixPath("/servlets", manager.start())
                            , Headers.SERVER_STRING, "U-tow"))
                    .setWorkerThreads(200)
                    .build()
                    .start();
        } catch (ServletException e) {
            e.printStackTrace();
        }

        try {
            registerInZookeeper("clientservice", System.getProperty("zookeeper.host"), System.getProperty("application.host"), Integer.parseInt(System.getProperty("application.port")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
}
