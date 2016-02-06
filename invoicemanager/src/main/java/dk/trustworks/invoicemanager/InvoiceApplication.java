package dk.trustworks.invoicemanager;

import com.google.common.net.MediaType;
import com.vaadin.server.VaadinServlet;
import dk.trustworks.framework.BaseApplication;
import dk.trustworks.framework.persistence.Helper;
import dk.trustworks.invoicemanager.handlers.InvoiceHandler;
import dk.trustworks.invoicemanager.handlers.ProductLineHandler;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
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
import java.util.Properties;

/**
 * Created by hans on 16/03/15.
 */
public class InvoiceApplication extends BaseApplication {

    public static final String JSON_UTF8 = MediaType.JSON_UTF_8.toString();

    static ServiceProvider serviceProvider;

    public static void main(String[] args) throws Exception {
        new InvoiceApplication(Integer.parseInt(args[0]));
    }

    public InvoiceApplication(int port) throws Exception {
        System.out.println("InvoiceManager on port " + port);
        Properties properties = new Properties();
        try (InputStream in = Helper.class.getResourceAsStream("server.properties")) {
            properties.load(in);
        }

        DeploymentInfo servletBuilder = Servlets.deployment()
                .setClassLoader(InvoiceApplication.class.getClassLoader())
                .setContextPath("/invoice")
                .setDeploymentName("invoice.war")
                .addServlets(
                        Servlets.servlet("invoiceServlet", VaadinServlet.class)
                                .addInitParam("UI", "dk.trustworks.invoicemanager.web.InvoiceUI")
                                .addMapping("/*"));

        DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();

        Undertow.builder()
                .addHttpListener(port, properties.getProperty("web.host"))
                .setBufferSize(1024 * 16)
                .setIoThreads(Runtime.getRuntime().availableProcessors() * 2) //this seems slightly faster in some configurations
                .setSocketOption(Options.BACKLOG, 10000)
                .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false) //don't send a keep-alive header for HTTP/1.1 requests, as it is not required
                .setServerOption(UndertowOptions.ALWAYS_SET_DATE, true)
                .setHandler(Handlers.header(Handlers.path()
                                .addPrefixPath("/api/invoices", new InvoiceHandler())
                                .addPrefixPath("/api/productlines", new ProductLineHandler())
                                .addPrefixPath("/invoice", manager.start())
                        , Headers.SERVER_STRING, "U-tow"))
                .setWorkerThreads(200)
                .build()
                .start();

        registerInZookeeper("invoiceservice", properties.getProperty("zookeeper.host"), port);
    }
}
