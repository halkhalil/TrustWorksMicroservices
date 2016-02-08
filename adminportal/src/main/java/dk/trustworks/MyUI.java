package dk.trustworks;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.*;
import com.vaadin.ui.UI;
import dk.trustworks.adminportal.server.VaadinBootstrapListener;
import dk.trustworks.adminportal.view.MenuDesign;
import dk.trustworks.framework.persistence.Helper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Theme("usermanagement")
@Widgetset("dk.trustworks.MyAppWidgetset")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        System.out.println("MyUI.init");
        System.out.println("System.getenv(\"ZK_SERVER_HOST\") = " + System.getenv("ZK_SERVER_HOST"));
        System.out.println("System.getProperty(\"ZK_SERVER_HOST\") = " + System.getProperty("ZK_SERVER_HOST"));
        setContent(new MenuDesign());
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
        @Override
        public void init(ServletConfig servletConfig) throws ServletException {
            super.init(servletConfig);
            /*
            try {
                registerInZookeeper("adminservice", System.getenv("ZK_SERVER_HOST"), System.getenv("ZK_APPLICATION_HOST"), Integer.parseInt(System.getenv("ZK_APPLICATION_PORT")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
        }

        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();
            getService().addSessionInitListener((SessionInitListener) sessionInitEvent -> sessionInitEvent.getSession().addBootstrapListener(new VaadinBootstrapListener()));
        }
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
