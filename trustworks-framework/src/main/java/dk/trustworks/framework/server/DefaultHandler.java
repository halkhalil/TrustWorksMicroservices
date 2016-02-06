package dk.trustworks.framework.server;

import com.codahale.metrics.*;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.framework.service.DefaultService;
import dk.trustworks.framework.service.ServiceRegistry;
import dk.trustworks.framework.servlets.MetricsServletContextListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by hans on 18/03/15.
 */
public abstract class DefaultHandler implements HttpHandler {

    private final MetricRegistry registry = MetricsServletContextListener.metricRegistry;

    private static final Logger logger = LogManager.getLogger();

    protected final ObjectMapper mapper;

    private final String entity;

    private final List<String> commands = new ArrayList<>();

    public DefaultHandler(String entity) {
        this.mapper = new ObjectMapper();
        this.entity = entity;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }
        ThreadContext.push(UUID.randomUUID().toString());

        logger.debug("handleRequest: " + entity);

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

        String[] relativePath = exchange.getRelativePath().split("/");
        if (relativePath.length == 0 || relativePath[relativePath.length - 1].equals("")) {
            switch (exchange.getRequestMethod().toString()) {
                case "GET":
                    getAllEntities(exchange);
                    break;
                case "POST":
                    createEntity(exchange);
                    break;
            }
        } else if (relativePath.length > 1 && relativePath[1].equals("search")) {
            logger.debug("DefaultHandler.handleRequest: SEARCH");
            logger.debug("relativePath[2] = " + relativePath[2]);
            handleSearch(exchange, relativePath[2]);
        } else if (relativePath.length > 1 && commands.contains(relativePath[1])) {
            logger.debug("DefaultHandler.handleRequest: " + relativePath[1]);
            executeCommand(exchange, relativePath);
        } else if (relativePath.length > 1) {
            switch (exchange.getRequestMethod().toString()) {
                case "GET":
                    logger.debug("DefaultHandler.handleRequest: GET");
                    logger.debug("relativePath = " + relativePath[1]);
                    findByUUID(exchange, relativePath[1]);
                    break;
                case "POST":
                    logger.debug("DefaultHandler.handleRequest: POST/UPDATE");
                    updateEntity(exchange, relativePath[1]);
                    break;
            }

        }
        ThreadContext.pop();
    }

    protected void getAllEntities(HttpServerExchange exchange) throws JsonProcessingException {
        final Timer timer = registry.timer(name(entity, "all", "response"));
        final Timer.Context context = timer.time();
        try {
            List<Map<String, Object>> allEntities = getService().getAllEntities(entity);
            if (exchange.getQueryParameters().get("projection") != null) {
                for (Map<String, Object> map : allEntities) {
                    for (String projectionTree : exchange.getQueryParameters().get("projection")) {
                        map.putAll(loadParentEntities(map, new LinkedList<>(Arrays.asList(projectionTree.split("/")))));
                    }
                }
            }
            if (exchange.getQueryParameters().get("children") != null) {
                try {
                    logger.debug("child projection");
                    for (Map<String, Object> map : allEntities) {
                        logger.debug("entity: " + entity);
                        loadChildEntities(map, new LinkedList<>(Arrays.asList(exchange.getQueryParameters().get("children").getFirst().split("/"))), entity + "uuid", 0);
                    }
                } catch (Exception e) {
                    logger.error("LOG00850:", e);
                }
            }
            exchange.getResponseSender().send(mapper.writeValueAsString(allEntities));
        } finally {
            context.stop();
        }
    }

    protected void findByUUID(HttpServerExchange exchange, String uuid) throws JsonProcessingException {
        final Timer timer = registry.timer(name(entity, "get", "response"));
        final Timer.Context context = timer.time();
        try {
            Map<String, Object> entity = getService().getOneEntity(this.entity, uuid);
            if (exchange.getQueryParameters().get("projection") != null) {
                for (String projectionTree : exchange.getQueryParameters().get("projection")) {
                    entity.putAll(loadParentEntities(entity, new LinkedList<>(Arrays.asList(projectionTree.split("/")))));
                }
            }
            exchange.getResponseSender().send(mapper.writeValueAsString(entity));
        } finally {
            context.stop();
        }
    }

    protected void createEntity(HttpServerExchange exchange) throws IOException, SQLException {
        final Timer timer = registry.timer(name(entity, "post", "response"));
        final Timer.Context context = timer.time();
        try {
            exchange.startBlocking();
            JsonNode jsonNode = mapper.readTree(exchange.getInputStream());
            getService().create(jsonNode);
        } finally {
            context.stop();
        }
    }

    protected void updateEntity(HttpServerExchange exchange, String uuid) throws IOException, SQLException {
        final Timer timer = registry.timer(name(entity, "update", "response"));
        final Timer.Context context = timer.time();
        try {
            exchange.startBlocking();
            JsonNode jsonNode = mapper.readTree(exchange.getInputStream());
            getService().update(jsonNode, uuid);
        } finally {
            context.stop();
        }
    }

    protected void handleSearch(HttpServerExchange exchange, String searchMethodName) throws Exception {
        logger.debug("DefaultHandler.handleSearch: " + this.getClass().toString());
        logger.debug("DefaultHandler.handleSearch: " + searchMethodName);
        final Timer timer = registry.timer(name(entity, "search", searchMethodName, "response"));
        final Timer.Context context = timer.time();
        try {
            Object result2 = getService().getClass().getDeclaredMethod(searchMethodName, Map.class).invoke(getService(), exchange.getQueryParameters());
            if (result2.getClass().equals(HashMap.class)) {
                exchange.getResponseSender().send(mapper.writeValueAsString(result2));
            } else {
                List<Map<String, Object>> result = (List<Map<String, Object>>) result2;
                if (exchange.getQueryParameters().get("projection") != null) {
                    for (Map<String, Object> map : result) {
                        for (String projectionTree : exchange.getQueryParameters().get("projection")) {
                            map.putAll(loadParentEntities(map, new LinkedList<>(Arrays.asList(projectionTree.split("/")))));
                        }
                    }
                }
                exchange.getResponseSender().send(mapper.writeValueAsString(result));
            }
        } finally {
            context.stop();
        }
    }

    private void executeCommand(HttpServerExchange exchange, String[] relativePath) throws Exception {
        final Timer timer = registry.timer(name(entity, "command", relativePath[1], "response"));
        final Timer.Context context = timer.time();
        try {
            this.getClass().getDeclaredMethod(relativePath[1], HttpServerExchange.class, String[].class).invoke(this, exchange, relativePath);
        } finally {
            context.stop();
        }
    }

    private Map<String, Object> loadParentEntities(Map<String, Object> map, List<String> projectionTree) {
        String key = projectionTree.remove(0);
        Map<String, Object> childEntities = new HashMap<>();
        Map<String, DefaultService> services = ServiceRegistry.getInstance().getServices();
        if (services.containsKey(key)) {
            if (map.size() == 0) return childEntities;
            Map<String, Object> projection = services.get(key).getOneEntity(services.get(key).getResourcePath(), map.get(key).toString());
            logger.debug("projection = " + projection);
            if (projectionTree.size() > 0) projection.putAll(loadParentEntities(projection, projectionTree));
            childEntities.put(key.substring(0, key.length() - 4), projection);
        }
        logger.debug("projectionTree = " + projectionTree.size());

        return childEntities;
    }

    private void loadChildEntities(Map<String, Object> map, List<String> projectionTree, String parentUUIDName, int level) {
        logger.debug("DefaultHandler.loadChildEntities");
        logger.debug("map = [" + map + "], projectionTree = [" + projectionTree + "], parentUUIDName = [" + parentUUIDName + "], level = [" + level + "]");
        String key = projectionTree.get(level);
        Map<String, DefaultService> services = ServiceRegistry.getInstance().getServices();
        if (services.containsKey(key)) {
            List<Map<String, Object>> children = services.get(key).findByParentUUID(services.get(key).getResourcePath(), parentUUIDName, (String) map.get("uuid"));
            logger.debug("children.size() = {}", children.size());
            if (children.size() == 0) return;
            map.put(key.replaceFirst("uuid", "s"), children);
            level++;
            if (level > projectionTree.size() - 1) return;
            for (Map<String, Object> child : children) {
                loadChildEntities(child, projectionTree, key, level);
            }
        }
        logger.debug("projectionTree = " + projectionTree.size());
    }

    protected abstract DefaultLocalService getService();

    protected void addCommand(String command) {
        commands.add(command);
    }
}
