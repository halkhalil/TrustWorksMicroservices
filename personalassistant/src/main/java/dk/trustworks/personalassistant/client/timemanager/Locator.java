package dk.trustworks.personalassistant.client.timemanager;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 25/04/15.
 */
public class Locator {

    private static Locator instance;

    private ServiceDiscovery<Object> serviceDiscovery;

    private Map<String, ServiceProvider> serviceProviders = new HashMap<>();

    private Locator() {
        String zkHost = System.getenv("zookeeper.host");
        if(zkHost == null) zkHost = System.getProperty("zookeeper.host");
        System.out.println("zkHost = " + zkHost);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zkHost+":2181", new RetryNTimes(5, 1000));
        curatorFramework.start();

        try {
            serviceDiscovery = ServiceDiscoveryBuilder
                    .builder(Object.class)
                    .basePath("trustworks")
                    .client(curatorFramework).build();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Locator getInstance() {
        return instance == null ? instance = new Locator() : instance;
    }

    public String resolveURL(String resource) {
        ServiceProvider serviceProvider;
        String uriSpec = "";
        if (!serviceProviders.containsKey(resource)) {
            serviceProvider = serviceDiscovery
                    .serviceProviderBuilder()
                    .serviceName(resource)
                    .build();
            serviceProviders.put(resource, serviceProvider);
            try {
                serviceProvider.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            serviceProvider = serviceProviders.get(resource);
        }
        try {
            uriSpec = serviceProvider.getInstance().buildUriSpec();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return uriSpec;
    }

/*
    private Locator() {
        String zkHost = System.getProperty("ZK_SERVER_HOST");
        if(zkHost == null) zkHost = System.getProperty("ZK_SERVER_HOST");
        System.out.println("zkHost = " + zkHost);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zkHost+":2181", new RetryNTimes(5, 1000));
        curatorFramework.start();

        try {
            serviceDiscovery = ServiceDiscoveryBuilder
                    .builder(Object.class)
                    .basePath("trustworks")
                    .client(curatorFramework).build();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Locator getInstance() {
        return instance == null ? instance = new Locator() : instance;
    }

    public String resolveURL(String resource) {
        ServiceProvider serviceProvider;
        String uriSpec = "";
        if (!serviceProviders.containsKey(resource)) {
            serviceProvider = serviceDiscovery
                    .serviceProviderBuilder()
                    .serviceName(resource)
                    .build();
            serviceProviders.put(resource, serviceProvider);
            try {
                serviceProvider.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            serviceProvider = serviceProviders.get(resource);
        }
        try {
            uriSpec = serviceProvider.getInstance().buildUriSpec();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return uriSpec;
    }
    */
}
