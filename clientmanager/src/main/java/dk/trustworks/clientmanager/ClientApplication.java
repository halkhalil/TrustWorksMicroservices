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
import dk.trustworks.framework.persistence.Helper;
import dk.trustworks.framework.service.ServiceRegistry;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
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
public class ClientApplication {

    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
        new ClientApplication(Integer.parseInt(args[0]));
    }

    public ClientApplication(int port) throws Exception {
        log.info("ClientManager on port " + port);
        Properties properties = new Properties();
        try (InputStream in = Helper.class.getResourceAsStream("server.properties")) {
            properties.load(in);
        }

        //cacheValues();

        ServiceRegistry serviceRegistry = ServiceRegistry.getInstance();

        serviceRegistry.registerService("taskworkerconstraintuuid", new TaskWorkerConstraintService());
        serviceRegistry.registerService("taskuuid", new TaskService());
        serviceRegistry.registerService("projectuuid", new ProjectService());
        serviceRegistry.registerService("clientuuid", new ClientService());
        serviceRegistry.registerService("useruuid", new UserService());

        Undertow.builder()
                .addHttpListener(port, properties.getProperty("web.host"))
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
                        , Headers.SERVER_STRING, "U-tow"))
                .setWorkerThreads(200)
                .build()
                .start();

        registerInZookeeper(properties.getProperty("zookeeper.host"), port);
    }

    private final void cacheValues() {
        ObjectMapper mapper = new ObjectMapper();
        HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();

        IMap<String, Client> clients = hzInstance.getMap("clients");
        for (Client entity : (List<Client>) mapper.convertValue(new ClientRepository().getAllEntities("client"), new TypeReference<List<Client>>() {
        })) {
            clients.put(entity.uuid, entity);
        }

        IMap<String, Project> projects = hzInstance.getMap("projects");
        projects.addIndex("clientUUID", true);
        for (Project project : (List<Project>) mapper.convertValue(new ProjectRepository().getAllEntities("project"), new TypeReference<List<Project>>() {
        })) {
            projects.put(project.getUUID(), project);
        }

        IMap<String, Task> tasks = hzInstance.getMap("tasks");
        tasks.addIndex("projectUUID", true);
        for (Task task : (List<Task>) mapper.convertValue(new TaskRepository().getAllEntities("task"), new TypeReference<List<Task>>() {
        })) {
            tasks.put(task.getUUID(), task);
        }

        IMap<String, TaskWorkerConstraint> taskWorkerConstraints = hzInstance.getMap("taskworkerconstraints");
        taskWorkerConstraints.addIndex("taskUUID", true);
        for (TaskWorkerConstraint taskWorkerConstraint : (List<TaskWorkerConstraint>) mapper.convertValue(new TaskWorkerConstraintRepository().getAllEntities("taskworkerconstraint"), new TypeReference<List<TaskWorkerConstraint>>() {
        })) {
            taskWorkerConstraints.put(taskWorkerConstraint.getUUID(), taskWorkerConstraint);
        }

        IMap<String, TaskWorkerConstraintBudget> taskWorkerConstraintBudgets = hzInstance.getMap("taskworkerconstraintbudgets");
        taskWorkerConstraintBudgets.addIndex("taskWorkerConstraintUUID", true);
        for (TaskWorkerConstraintBudget taskWorkerConstraintBudget : (List<TaskWorkerConstraintBudget>) mapper.convertValue(new TaskWorkerConstraintBudgetRepository().getAllEntities("taskworkerconstraintbudget"), new TypeReference<List<TaskWorkerConstraintBudget>>() {
        })) {
            taskWorkerConstraintBudgets.put(taskWorkerConstraintBudget.getUuid(), taskWorkerConstraintBudget);
        }

    }

    private static void registerInZookeeper(String zooHost, int port) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zooHost + ":2181", new RetryNTimes(5, 1000));
        curatorFramework.start();

        ServiceInstance serviceInstance = ServiceInstance.builder()
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .address("localhost")
                .port(port)
                .name("clientservice")
                .build();

        ServiceDiscoveryBuilder.builder(Object.class)
                .basePath("trustworks")
                .client(curatorFramework)
                .thisInstance(serviceInstance)
                .build()
                .start();
    }
}
