package dk.trustworks.financemanager;

import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.typesafe.config.Config;
import dk.trustworks.financemanager.model.Expense;
import dk.trustworks.financemanager.service.ExpensesService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.jooby.Jooby;
import org.jooby.jdbc.Jdbc;
import org.jooby.json.Jackson;
import org.jooby.metrics.Metrics;
import org.jooby.swagger.SwaggerUI;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by hans on 20/01/16.
 */
public class App extends Jooby {

    {
        try {
            Properties properties = new Properties();
            try (InputStream in = new FileInputStream("server.properties")) {
                properties.load(in);
            }
            System.out.println("properties.getProperty(\"zookeeper.host\") = " + properties.getProperty("zookeeper.host"));
            registerInZookeeper(properties.getProperty("zookeeper.host"), Integer.parseInt(properties.getProperty("zookeeper.port")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        use(new Jdbc());
        use(new Jackson());
        assets("/**");
        assets("/", "assets/index.html");
        SwaggerUI.install(this);

        //use(ExpensesService.class);
        get("/api/expenses", (req, resp) -> {
            DataSource db = req.require(DataSource.class);
            resp.send(new ExpensesService(db).root());
        }).name("Get all expenses");


        post("/api/expenses", (req, resp) -> {
            DataSource db = req.require(DataSource.class);
            new ExpensesService(db).create(req.body().to(Expense.class));
            resp.send("ok");
        }).name("Post new Expense");

        post("/api/expenses/:uuid", (req, resp) -> {
            DataSource db = req.require(DataSource.class);
            new ExpensesService(db).update(req.body().to(Expense.class));
            resp.send("ok");
        }).name("Post new Expense");

        get("/api/expenses/search/findByYear", (req, resp) -> {
            DataSource db = req.require(DataSource.class);
            resp.send(new ExpensesService(db).findByYear(req.param("year").intValue()));
        }).name("Find Expenses by Year");



        use(new Metrics()
                .request()
                .threadDump()
                .ping()
                .metric("memory", new MemoryUsageGaugeSet())
                .metric("threads", new ThreadStatesGaugeSet())
                .metric("gc", new GarbageCollectorMetricSet())
                .metric("fs", new FileDescriptorRatioGauge())
        );
    }

    public static void main(final String[] args) throws Exception {
        new App().start();
    }

    private static void registerInZookeeper(String zooHost, int port) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zooHost + ":2181", new RetryNTimes(5, 1000));
        curatorFramework.start();

        ServiceInstance serviceInstance = ServiceInstance.builder()
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .address("localhost")
                .port(port)
                .name("financeservice")
                .build();

        ServiceDiscoveryBuilder.builder(Object.class)
                .basePath("trustworks")
                .client(curatorFramework)
                .thisInstance(serviceInstance)
                .build()
                .start();
    }
}
