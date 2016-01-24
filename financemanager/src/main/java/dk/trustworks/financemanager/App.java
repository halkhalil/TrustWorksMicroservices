package dk.trustworks.financemanager;

import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
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

/**
 * Created by hans on 20/01/16.
 */
public class App extends Jooby {

    {
        try {
            registerInZookeeper("172.31.46.235", 9098);
        } catch (Exception e) {
            e.printStackTrace();
        }
        use(new Jdbc());
        use(new Jackson());
        assets("/**");
        assets("/", "assets/index.html");
        SwaggerUI.install(this);

        use(ExpensesService.class);

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
        new App().start(args); // 3. start the application.
    }

    private static void registerInZookeeper(String zooHost, int port) throws Exception {
        //zooHost = "172.31.46.235";
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zooHost + ":2181", new RetryNTimes(5, 1000));
        curatorFramework.start();

        ServiceInstance serviceInstance = ServiceInstance.builder()
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .address("172.31.47.37")
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
