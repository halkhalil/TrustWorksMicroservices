package dk.trustworks.usermanager;

import com.google.common.net.MediaType;
import com.vaadin.server.VaadinServlet;
import dk.trustworks.framework.BaseApplication;
import dk.trustworks.framework.persistence.Helper;
import dk.trustworks.usermanager.handlers.UserHandler;
import dk.trustworks.usermanager.web.UserList;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.cache.DirectBufferCache;
import io.undertow.server.handlers.resource.*;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
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

import java.io.File;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.servlet.Servlets.servlet;

/**
 * Created by hans on 16/03/15.
 */
public class UserApplication extends BaseApplication {

    public static final String JSON_UTF8 = MediaType.JSON_UTF_8.toString();

    static ServiceProvider serviceProvider;

    public static void main(String[] args) throws Exception {
        new UserApplication(Integer.parseInt(args[0]));
    }

    public UserApplication(int port) throws Exception {
        System.out.println("UserManager on port " + port);
        Properties properties = new Properties();
        try (InputStream in = Helper.class.getResourceAsStream("server.properties")) {
            properties.load(in);
        }

        DeploymentManager manager = getMetricsDeploymentManager();
        manager.deploy();

        Undertow.builder()
                .addHttpListener(port, properties.getProperty("web.host"))
                .setBufferSize(1024 * 16)
                .setIoThreads(Runtime.getRuntime().availableProcessors() * 2) //this seems slightly faster in some configurations
                .setSocketOption(Options.BACKLOG, 10000)
                .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false) //don't send a keep-alive header for HTTP/1.1 requests, as it is not required
                .setServerOption(UndertowOptions.ALWAYS_SET_DATE, true)
                .setHandler(Handlers.header(Handlers.path()
                        .addPrefixPath("/api/users", new UserHandler())
                        .addPrefixPath("/servlets", manager.start())
                        , Headers.SERVER_STRING, "U-tow"))
                .setWorkerThreads(200)
                .build()
                .start();

        registerInZookeeper("userservice", properties.getProperty("zookeeper.host"), port);
    }
}
