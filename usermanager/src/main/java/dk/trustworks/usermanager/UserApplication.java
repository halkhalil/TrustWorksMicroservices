package dk.trustworks.usermanager;

import com.google.common.net.MediaType;
import com.vaadin.server.VaadinServlet;
import dk.trustworks.framework.persistence.Helper;
import dk.trustworks.usermanager.handlers.UserHandler;
import dk.trustworks.usermanager.web.UserList;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.cache.DirectBufferCache;
import io.undertow.server.handlers.resource.CachingResourceManager;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
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

import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

/**
 * Created by hans on 16/03/15.
 */
public class UserApplication {

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

        DeploymentInfo servletBuilder = Servlets.deployment()
                .setClassLoader(UserApplication.class.getClassLoader())
                .setContextPath("/myapp")
                .setDeploymentName("test.war")
                .addServlets(
                        Servlets.servlet("myservlet", VaadinServlet.class)
                                .addInitParam("UI", "dk.trustworks.usermanager.web.UserListUI")
                                .addMapping("/*"));


        DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();
        //PathHandler path = Handlers.path(Handlers.redirect("/myapp")).addPrefixPath("/myapp", manager.start());

        Undertow.builder()
                .addHttpListener(port, properties.getProperty("web.host"))
                .setBufferSize(1024 * 16)
                .setIoThreads(Runtime.getRuntime().availableProcessors() * 2) //this seems slightly faster in some configurations
                .setSocketOption(Options.BACKLOG, 10000)
                .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false) //don't send a keep-alive header for HTTP/1.1 requests, as it is not required
                .setServerOption(UndertowOptions.ALWAYS_SET_DATE, true)
                .setHandler(Handlers.header(Handlers.path()
                        .addPrefixPath("/api/users", new UserHandler())
                        .addPrefixPath("/html", createStaticResourceHandler())
                        .addPrefixPath("/myapp", manager.start()), Headers.SERVER_STRING, "U-tow"))
                .setWorkerThreads(200)
                .build()
                .start();

        registerInZookeeper(properties.getProperty("zookeeper.host"), port);
    }

    private HttpHandler createStaticResourceHandler() {
        final ResourceManager staticResources =
                new ClassPathResourceManager(UserList.class.getClassLoader(), "dk/trustworks/usermanager/web");
        // Cache tuning is copied from Undertow unit tests.
        final ResourceManager cachedResources =
                new CachingResourceManager(100, 65536,
                        new DirectBufferCache(1024, 10, 10480),
                        staticResources,
                        (int) Duration.ofDays(1).getSeconds());
        final ResourceHandler resourceHandler = new ResourceHandler(cachedResources);
        resourceHandler.setWelcomeFiles("index.html");
        return resourceHandler;
    }

    private static void registerInZookeeper(String zooHost, int port) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zooHost + ":2181", new RetryNTimes(5, 1000));
        curatorFramework.start();

        ServiceInstance serviceInstance = ServiceInstance.builder()
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .address("localhost")
                .port(port)
                .name("userservice")
                .build();

        ServiceDiscoveryBuilder.builder(Object.class)
                .basePath("trustworks")
                .client(curatorFramework)
                .thisInstance(serviceInstance)
                .build()
                .start();
    }
}
