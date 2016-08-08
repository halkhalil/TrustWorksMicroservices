package dk.trustworks.clientmanager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import dk.trustworks.clientmanager.handlers.*;
import dk.trustworks.clientmanager.persistence.*;
import dk.trustworks.clientmanager.service.*;
import dk.trustworks.distributed.model.*;
import dk.trustworks.framework.BaseApplication;
import dk.trustworks.framework.persistence.Helper;
import dk.trustworks.framework.service.ServiceRegistry;
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
import org.apache.curator.x.discovery.UriSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xnio.Options;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by hans on 16/03/15.
 */
public class ClientApplication extends BaseApplication {

    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
        new ClientApplication();
    }

    public ClientApplication() throws Exception {
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

        registerInZookeeper("clientservice", System.getProperty("zookeeper.host"), System.getProperty("application.host"), Integer.parseInt(System.getProperty("application.port")));
    }
}
