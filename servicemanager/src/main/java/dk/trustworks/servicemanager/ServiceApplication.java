package dk.trustworks.servicemanager;

import dk.trustworks.servicemanager.security.MapIdentityManager;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.handlers.AuthenticationCallHandler;
import io.undertow.security.handlers.AuthenticationConstraintHandler;
import io.undertow.security.handlers.AuthenticationMechanismsHandler;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.BasicAuthenticationMechanism;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.proxy.ProxyHandler;
import io.undertow.server.handlers.proxy.SimpleProxyClientProvider;
import io.undertow.util.Headers;
import org.apache.curator.x.discovery.ServiceProvider;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 16/03/15.
 */
public class ServiceApplication {

    static ServiceProvider serviceProvider;

    public static void main(String[] args) throws Exception {
        new ServiceApplication();
    }

    public ServiceApplication() throws Exception {
        Undertow reverseProxy = null;
        ClientProxyZookeeper userManagerProxy = null;
        ClientProxyZookeeper clientManagerProxy = null;
        ClientProxyZookeeper biManagerProxy = null;
        ClientProxyZookeeper financeProxy = null;
        ClientProxyZookeeper timeManagerProxy = null;
        try {
            System.out.println("Application");
            System.out.println("System.getenv(\"APPLICATION_URL\") = " + System.getenv("APPLICATION_URL"));
            System.out.println("System.getenv(\"ZOOKEEPER_URL\") = " + System.getenv("ZOOKEEPER_URL"));
            System.out.println("System.getenv(\"APPLICATION_PORT\") = " + System.getenv("APPLICATION_PORT"));
            System.out.println("System.getenv(\"PORT\") = " + System.getenv("PORT"));
            System.out.println("System.getenv(\"HOST\") = " + System.getenv("HOST"));
            System.out.println("System.getProperty(\"PORT\") = " + System.getProperty("PORT"));
            System.out.println("System.getProperty(\"HOST\") = " + System.getProperty("HOST"));
            userManagerProxy = new ClientProxyZookeeper("userservice");
            clientManagerProxy = new ClientProxyZookeeper("clientservice");
            timeManagerProxy = new ClientProxyZookeeper("timeservice");
            biManagerProxy = new ClientProxyZookeeper("biservice");
            ClientProxyZookeeper adminPortalProxy = new ClientProxyZookeeper("adminportal");
            financeProxy = new ClientProxyZookeeper("financeservice");

            final Map<String, char[]> users = new HashMap<>(2);
            users.put("userOne", "passwordOne".toCharArray());
            users.put("userTwo", "passwordTwo".toCharArray());

            final IdentityManager identityManager = new MapIdentityManager(users);

            reverseProxy = Undertow.builder()
                    .addHttpListener(Integer.parseInt(System.getenv("PORT")), System.getenv("APPLICATION_URL"))
                    .setIoThreads(4)
                    .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                    .setServerOption(UndertowOptions.ENABLE_SPDY, true)
                    .setHandler(Handlers.path()
                            .addPrefixPath("/userservice", new ProxyHandler(userManagerProxy, 30000, ResponseCodeHandler.HANDLE_404))
                            .addPrefixPath("/clientservice", new ProxyHandler(clientManagerProxy, 30000, ResponseCodeHandler.HANDLE_404))
                            .addPrefixPath("/biservice", new ProxyHandler(biManagerProxy, 30000, ResponseCodeHandler.HANDLE_404))
                            .addPrefixPath("/timeservice", new ProxyHandler(timeManagerProxy, 30000, ResponseCodeHandler.HANDLE_404))
                            .addPrefixPath("/financeservice", new ProxyHandler(financeProxy, 30000, ResponseCodeHandler.HANDLE_404))
                            .addPrefixPath("/version", new VersionHandler())
                            .addPrefixPath("/", new ProxyHandler(new SimpleProxyClientProvider(new URI("http://localhost:9099")), 30000, ResponseCodeHandler.HANDLE_404)))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            reverseProxy.start();
            System.out.println("Running on port 80");
        } catch (Exception e) {
            e.printStackTrace();
            reverseProxy = Undertow.builder()
                    .addHttpListener(9090, "localhost")
                    .setIoThreads(4)
                    .setHandler(Handlers.path()
                            .addPrefixPath("/userservice", new ProxyHandler(userManagerProxy, 30000, ResponseCodeHandler.HANDLE_404))
                            .addPrefixPath("/clientservice", new ProxyHandler(clientManagerProxy, 30000, ResponseCodeHandler.HANDLE_404))
                            .addPrefixPath("/biservice", new ProxyHandler(biManagerProxy, 30000, ResponseCodeHandler.HANDLE_404))
                            .addPrefixPath("/timeservice", new ProxyHandler(timeManagerProxy, 30000, ResponseCodeHandler.HANDLE_404))
                            .addPrefixPath("/financeservice", new ProxyHandler(financeProxy, 30000, ResponseCodeHandler.HANDLE_404))
                            .addPrefixPath("/version", new VersionHandler())
                            .addPrefixPath("/", new ProxyHandler(new SimpleProxyClientProvider(new URI("http://localhost:9099")), 30000, ResponseCodeHandler.HANDLE_404)))
                    .build();
            reverseProxy.start();
            System.out.println("Running on port 9090");
        }
    }

    private static HttpHandler addSecurity(final HttpHandler toWrap, final IdentityManager identityManager) {
        HttpHandler handler = toWrap;
        handler = new AuthenticationCallHandler(handler);
        handler = new AuthenticationConstraintHandler(handler);
        final List<AuthenticationMechanism> mechanisms = Collections.<AuthenticationMechanism>singletonList(new BasicAuthenticationMechanism("My Realm"));
        handler = new AuthenticationMechanismsHandler(handler, mechanisms);
        handler = new SecurityInitialHandler(AuthenticationMode.PRO_ACTIVE, identityManager, handler);
        return handler;
    }

    class VersionHandler implements HttpHandler {
        @Override
        public void handleRequest(final HttpServerExchange exchange) throws Exception {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("{version:2.0.5}");
        }
    }
}
