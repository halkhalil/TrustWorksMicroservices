package dk.trustworks.framework;

import com.codahale.metrics.servlets.MetricsServlet;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.netflix.hystrix.contrib.requestservlet.HystrixRequestContextServletFilter;
import dk.trustworks.framework.servlets.MetricsServletContextListener;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;

import javax.servlet.DispatcherType;

/**
 * Created by hans on 06/02/16.
 */
public class BaseApplication {
    protected DeploymentManager getMetricsDeploymentManager() {
        DeploymentInfo servletBuilder = Servlets.deployment()
                .setClassLoader(this.getClass().getClassLoader())
                .setContextPath("/servlets")
                .setDeploymentName("test.war")
                .addServlets(
                        Servlets.servlet("HystrixMetricsStreamServlet", HystrixMetricsStreamServlet.class)
                                .addMapping("/hystrix.stream"),
                        Servlets.servlet("MetricsServlet", MetricsServlet.class)
                                .addMapping("/metrics"))
                .addListener(Servlets.listener(MetricsServletContextListener.class))
                .addFilter(
                        Servlets.filter("HystrixRequestContextServletFilter", HystrixRequestContextServletFilter.class))

                .addFilterUrlMapping("HystrixRequestContextServletFilter", "/*", DispatcherType.REQUEST);


        DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();
        return manager;
    }

    protected static void registerInZookeeper(String serviceName, String zooHost, int port) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zooHost + ":2181", new RetryNTimes(5, 1000));
        curatorFramework.start();

        ServiceInstance serviceInstance = ServiceInstance.builder()
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .address(System.getenv("APPLICATION_HOST"))
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
