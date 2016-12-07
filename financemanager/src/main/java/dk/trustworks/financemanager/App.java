package dk.trustworks.financemanager;

import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import dk.trustworks.financemanager.model.Expense;
import dk.trustworks.financemanager.service.ExpensesService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.jooby.Jooby;
import org.jooby.Result;
import org.jooby.Results;
import org.jooby.jdbc.Jdbc;
import org.jooby.json.Jackson;
import org.jooby.metrics.Metrics;
import org.jooby.swagger.SwaggerUI;

import javax.sql.DataSource;

/**
 * Created by hans on 20/01/16.
 */
public class App extends Jooby {

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
        try {
            registerInZookeeper("financeservice", System.getProperty("zookeeper.host"), System.getProperty("application.host"), Integer.parseInt(System.getProperty("application.port")));
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        System.setProperty("db.url", System.getenv("DATABASE_URI"));
        System.setProperty("db.user", System.getenv("DATABASE_USER"));
        System.setProperty("db.password", System.getenv("DATABASE_PASS"));
        System.setProperty("application.port", System.getenv("FINANCESERVICE_PORT"));
        System.setProperty("application.host", System.getenv("APPLICATION_URL"));
*/
        use(new Jdbc());
        use(new Jackson());

        get("/", () -> HOME);

        //assets("/**");
        //assets("/", "assets/index.html");

        use("/api/expenses")
                .get("/", (req, resp) -> {
                    LocalDate periodStart = LocalDate.parse(req.param("periodStart").value("2016-01-01"), DateTimeFormat.forPattern("yyyy-MM-dd"));
                    LocalDate periodEnd = LocalDate.parse(req.param("periodEnd").value("2016-12-31"), DateTimeFormat.forPattern("yyyy-MM-dd"));

                    DataSource db = req.require(DataSource.class);
                    resp.send(new ExpensesService(db).root(periodStart, periodEnd));
                }).name("Get all expenses")


                .post("/", (req, resp) -> {
                    DataSource db = req.require(DataSource.class);
                    new ExpensesService(db).create(req.body().to(Expense.class));
                    resp.send("ok");
                }).name("Post new Expense")

                .post("/:uuid", (req, resp) -> {
                    DataSource db = req.require(DataSource.class);
                    new ExpensesService(db).update(req.body().to(Expense.class));
                    resp.send("ok");
                }).name("Post new Expense")

                .get("/search/findByYear", (req, resp) -> {
                    DataSource db = req.require(DataSource.class);
                    resp.send(new ExpensesService(db).findByYear(req.param("year").intValue()));
                }).name("Find Expenses by Year")

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

    public static void main(final String[] args) throws Exception {
        new App().start();
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
