package dk.trustworks.employeemanager;

import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import dk.trustworks.employeemanager.dto.Employee;
import dk.trustworks.employeemanager.persistence.EmployeeRepository;
import dk.trustworks.employeemanager.services.EmployeeService;
import dk.trustworks.employeemanager.services.StatusPeriodService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.joda.time.LocalDate;
import org.jooby.*;
import org.jooby.flyway.Flywaydb;
import org.jooby.jdbc.Jdbc;
import org.jooby.json.Jackson;
import org.jooby.metrics.Metrics;
import org.jooby.raml.Raml;
import org.jooby.swagger.SwaggerUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * Created by hans on 13/07/16.
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
        Logger log = LoggerFactory.getLogger("dk.trustworks.employeemanager");

        try {
            registerInZookeeper("employeeservice", System.getenv("ZK_SERVER_HOST"), System.getenv("ZK_APPLICATION_HOST"), Integer.parseInt(System.getenv("ZK_APPLICATION_PORT")));
        } catch (Exception e) {
            log.error("Zookeeper registration", e);
        }

        System.setProperty("db.url", System.getenv("DATABASE_URI"));
        System.setProperty("db.user", System.getenv("DATABASE_USER"));
        System.setProperty("db.password", System.getenv("DATABASE_PASS"));
        System.setProperty("flyway.url", System.getenv("DATABASE_URI"));
        System.setProperty("flyway.user", System.getenv("DATABASE_USER"));
        System.setProperty("flyway.password", System.getenv("DATABASE_PASS"));
        System.setProperty("application.port", System.getenv("PORT"));
        System.setProperty("application.host", System.getenv("APPLICATION_URL"));

        use(new Flywaydb());
        use(new Jdbc());
        use(new Jackson().module(new JodaModule()));
        use(EmployeeRepository.class);

        use(new Metrics()
                .request()
                .threadDump()
                .ping()
                .metric("memory", new MemoryUsageGaugeSet())
                .metric("threads", new ThreadStatesGaugeSet())
                .metric("gc", new GarbageCollectorMetricSet())
                .metric("fs", new FileDescriptorRatioGauge())
        );

        //use(new Auth().basic("*", Authenticator.class));

        get("/", () -> HOME);

        /**
         *
         * Everything about your Employees.
         */
        use("/api/employees")
            /**
             *
             * List employees ordered by id.
             *
             * @param start Start offset, useful for paging. Default is <code>0</code>.
             * @param max Max page size, useful for paging. Default is <code>50</code>.
             * @param username Filter by username. Default is no filter.
             * @return Employees ordered by id.
             */
            .get(req -> {
                log.debug("/api/employees requested");
                int start = req.param("start").intValue(0);
                log.debug("start = " + start);
                int max = req.param("max").intValue(50);
                String username = req.param("username").value("%");
                log.debug("max = " + max);

                DataSource db = req.require(DataSource.class);
                return new EmployeeService(db).findAll();
            })

            /**
             *
             * Find employee by UUID
             *
             * @param uuid Employee UUID.
             * @return Returns <code>200</code> with a single employee or <code>404</code>
             */
            .get("/:uuid", req -> {
                String uuid = req.param("uuid").value();
                DataSource db = req.require(DataSource.class);
                Employee employee = new EmployeeService(db).find(uuid);
                if (employee == null) {
                    throw new Err(Status.NOT_FOUND);
                }
                return employee;
            })
            .produces("json")
            .consumes("json");

        /**
         * Employee status during a given period
         */
        use("/api/statusperiods")
                /**
                 *
                 * List employees status during a given period.
                 *
                 * @param fromDate Start date for period <code>YYYY-MONTH-DAY</code>. Default is january current year.
                 * @param toDate End date for period <code>YYYY-MONTH-DAY</code>. Default is december current year.
                 * @return StatusPeriod.
                 */
                .get(req -> {
                    LocalDate fromDate = LocalDate.parse(req.param("fromDate").value(LocalDate.now().withDayOfYear(1).minusYears(1).toString("yyyy-MM-dd")));
                    LocalDate toDate = LocalDate.parse(req.param("toDate").value(LocalDate.now().withMonthOfYear(12).withDayOfMonth(31).minusYears(1).toString("yyyy-MM-dd")));

                    DataSource db = req.require(DataSource.class);
                    return new StatusPeriodService(db).findAllByPeriod(fromDate, toDate);
                })
                .produces("json")
                .consumes("json");

        new Raml().install(this);
        new SwaggerUI().tag(route -> "employees").install(this);

    }

    public static void main(final String[] args) throws Throwable {
        new App().start(args);
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
