package dk.trustworks.timemanager;

import com.google.common.net.MediaType;
import dk.trustworks.framework.BaseApplication;
import dk.trustworks.framework.persistence.Helper;
import dk.trustworks.framework.service.ServiceRegistry;
import dk.trustworks.timemanager.handlers.TaskWeekViewHandler;
import dk.trustworks.timemanager.handlers.WeekHandler;
import dk.trustworks.timemanager.handlers.WorkHandler;
import dk.trustworks.timemanager.service.ClientService;
import dk.trustworks.timemanager.service.ProjectService;
import dk.trustworks.timemanager.service.TaskService;
import dk.trustworks.timemanager.service.UserService;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.util.Headers;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.UriSpec;
import org.xnio.Options;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by hans on 16/03/15.
 */
public class TimeApplication extends BaseApplication {

    public static void main(String[] args) throws Exception {
        new TimeApplication(Integer.parseInt(args[0]));
    }

    public TimeApplication(int port) throws Exception {
        System.out.println("TimeManager on port " + port);
        Properties properties = new Properties();
        try (InputStream in = Helper.class.getResourceAsStream("server.properties")) {
            properties.load(in);
        }

        DeploymentManager manager = getMetricsDeploymentManager();

        ServiceRegistry serviceRegistry = ServiceRegistry.getInstance();

        serviceRegistry.registerService("taskuuid", new TaskService());
        serviceRegistry.registerService("useruuid", new UserService());
        serviceRegistry.registerService("projectuuid", new ProjectService());
        serviceRegistry.registerService("clientuuid", new ClientService());

        Undertow.builder()
                .addHttpListener(port, properties.getProperty("web.host"))
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

        registerInZookeeper("timeservice", properties.getProperty("zookeeper.host"), port);
    }
}
