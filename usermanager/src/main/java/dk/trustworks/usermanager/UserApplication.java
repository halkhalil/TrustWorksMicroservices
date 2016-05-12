package dk.trustworks.usermanager;

import com.google.common.net.MediaType;
import dk.trustworks.framework.BaseApplication;
import dk.trustworks.usermanager.handlers.SalaryHandler;
import dk.trustworks.usermanager.handlers.UserHandler;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.util.Headers;
import org.apache.curator.x.discovery.ServiceProvider;
import org.xnio.Options;

/**
 * Created by hans on 16/03/15.
 */
public class UserApplication extends BaseApplication {

    public static final String JSON_UTF8 = MediaType.JSON_UTF_8.toString();

    static ServiceProvider serviceProvider;

    public static void main(String[] args) throws Exception {
        new UserApplication();
    }

    public UserApplication() throws Exception {
        printEnvironment();

        DeploymentManager manager = getMetricsDeploymentManager();
        manager.deploy();

        Undertow.builder()
                .addHttpListener(Integer.parseInt(System.getenv("PORT")), System.getenv("APPLICATION_HOST"))
                .setBufferSize(1024 * 16)
                .setIoThreads(Runtime.getRuntime().availableProcessors() * 2) //this seems slightly faster in some configurations
                .setSocketOption(Options.BACKLOG, 10000)
                .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false) //don't send a keep-alive header for HTTP/1.1 requests, as it is not required
                .setServerOption(UndertowOptions.ALWAYS_SET_DATE, true)
                .setHandler(Handlers.header(Handlers.path()
                        .addPrefixPath("/api/users", new UserHandler())
                        .addPrefixPath("/api/salaries", new SalaryHandler())
                        .addPrefixPath("/servlets", manager.start())
                        , Headers.SERVER_STRING, "U-tow"))
                .setWorkerThreads(200)
                .build()
                .start();

        //registerInZookeeper("userservice", properties.getProperty("zookeeper.host"), port);
        registerInZookeeper("userservice", System.getenv("ZK_SERVER_HOST"), System.getenv("ZK_APPLICATION_HOST"), Integer.parseInt(System.getenv("ZK_APPLICATION_PORT")));
    }
}
