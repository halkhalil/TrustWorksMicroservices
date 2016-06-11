package dk.trustworks.personalassistant;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import dk.trustworks.personalassistant.commands.FileSearchCommand;
import dk.trustworks.personalassistant.dto.slack.SlackMessage;
import dk.trustworks.personalassistant.dto.slack.SlackSlashCommand;
import dk.trustworks.personalassistant.search.indexers.Searcher;
import dk.trustworks.personalassistant.service.CommandService;
import dk.trustworks.personalassistant.topics.*;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.jooby.Jooby;
import org.jooby.MediaType;
import org.jooby.exec.Exec;
import org.jooby.json.Jackson;
import org.jooby.raml.Raml;
import org.jooby.swagger.SwaggerUI;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by hans on 20/01/16.
 */
public class MotherApplication extends Jooby {

    public static final MetricRegistry metricRegistry = new MetricRegistry();
    private transient ObjectMapper metricsMapper;

    static Topic topics[] = {new Mother(), new Pictures(), new Templates(), new FileSearch(), new Connect()};

    {
        this.metricsMapper = new ObjectMapper().registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, true));

        try {
            registerInZookeeper("motherservice", System.getenv("ZK_SERVER_HOST"), System.getenv("ZK_APPLICATION_HOST"), Integer.parseInt(System.getenv("ZK_APPLICATION_PORT")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.setProperty("application.port", System.getenv("PORT"));
        System.setProperty("application.host", System.getenv("APPLICATION_URL"));
        System.setProperty("slack.token", System.getenv("SLACK_TOKEN"));

        //use(new Jdbc());
        use(new Jackson());
        use(new Exec());

        use("/api/commands")
                .get("/", (req, resp) -> {
                    System.out.println("req = " + req);
                    final Timer timer = metricRegistry.timer(name("command", "all", "response"));
                    final Timer.Context context = timer.time();
                    try {
                        resp.send("ok");
                    } finally {
                        context.stop();
                    }
                })
                .post("/", (req, resp) -> {
                    final Timer timer = metricRegistry.timer(name("command", "create", "response"));
                    final Timer.Context context = timer.time();
                    try {
                        ExecutorService executor = req.require(ExecutorService.class);
                        SlackSlashCommand command = req.body().to(SlackSlashCommand.class);

                        executor.execute(() -> new CommandService().create(command));
                        resp.status(200).send(new SlackMessage("in_channel"));

                        /*
                        if(!command.channel_name.equals("directmessage")) {
                            resp.status(200).send(new SlackMessage("Some responses ", "ephemeral"));
                        }
                        resp.status(200).send(new SlackMessage("in_channel"));
                        */
                    } finally {
                        context.stop();
                    }
                }).produces(MediaType.json);

        use("/servlets/metrics")
                .get("/", (req, resp) -> {
                    resp.send(metricsMapper.valueToTree(metricRegistry));
                });


        new Raml().install(this);
        new SwaggerUI().install(this);
    }

    public static void main(final String[] args) throws Throwable {
        new MotherApplication().start();

        Unirest.setObjectMapper(new com.mashape.unirest.http.ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        //slackClient();
    }
/*
    public static void slackClient() {
        SlackRealTimeMessagingClient slackRealTimeMessagingClient = SlackClientFactory.createSlackRealTimeMessagingClient("xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61");
        SlackWebhookClient webhookClient = SlackClientFactory.createWebhookClient("https://hooks.slack.com/services/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");
        SlackWebApiClient webApiClient = SlackClientFactory.createWebApiClient("xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61");
        for (Channel channel1 : webApiClient.getChannelList()) {
            System.out.println("channel1.getName() = " + channel1.getName());
            System.out.println("channel1.getId() = " + channel1.getId());
        }

        slackRealTimeMessagingClient.addListener(Event.MESSAGE,jsonNode -> {
            if(!jsonNode.get("text").asText().contains("<@U13EEAATT>")) return;
            System.out.println("jsonNode = " + jsonNode);
            String command = jsonNode.get("text").asText();
            Topic actualTopic = null;
            double highScore = 0.0;
            for (Topic topic : topics) {
                double probability = topic.getProbability(command);
                if(probability> 0.8 && (actualTopic == null || probability > highScore)) {
                    actualTopic = topic;
                    highScore = probability;
                }
            }
            if(actualTopic != null) {
                System.out.println("actualTopic = " + actualTopic);
                actualTopic.postResponse(command, jsonNode.get("channel").asText(), jsonNode.get("user").asText());
            }
        });

        slackRealTimeMessagingClient.connect();
    }
*/
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

/*
{
    "ok": true,
    "ims": [
        {
            "id": "D13E1A10E",
            "is_im": true,
            "user": "USLACKBOT",
            "created": 1461591394,
            "is_org_shared": false,
            "is_user_deleted": false
        },
        {
            "id": "D13E9H2N9",
            "is_im": true,
            "user": "U036JELTN",
            "created": 1461591394,
            "is_org_shared": false,
            "is_user_deleted": false
        },
        {
            "id": "D13E1A3E2",
            "is_im": true,
            "user": "U036SDG91",
            "created": 1461591395,
            "is_org_shared": false,
            "is_user_deleted": false
        },
        {
            "id": "D13EL1UGJ",
            "is_im": true,
            "user": "U036T85S8",
            "created": 1461591395,
            "is_org_shared": false,
            "is_user_deleted": false
        },
        {
            "id": "D13EL1VBL",
            "is_im": true,
            "user": "U036UJGD3",
            "created": 1461591395,
            "is_org_shared": false,
            "is_user_deleted": false
        },
        {
            "id": "D13CT98G3",
            "is_im": true,
            "user": "U03E84SPW",
            "created": 1461591395,
            "is_org_shared": false,
            "is_user_deleted": false
        },
        {
            "id": "D13EL1UR0",
            "is_im": true,
            "user": "U04VA95H7",
            "created": 1461591395,
            "is_org_shared": false,
            "is_user_deleted": false
        },
        {
            "id": "D13E72HT2",
            "is_im": true,
            "user": "U07R3T65U",
            "created": 1461591395,
            "is_org_shared": false,
            "is_user_deleted": false
        },
        {
            "id": "D13E72J8Y",
            "is_im": true,
            "user": "U0DRC1QR2",
            "created": 1461591395,
            "is_org_shared": false,
            "is_user_deleted": false
        },
        {
            "id": "D13EL1UD8",
            "is_im": true,
            "user": "U0KUFG50D",
            "created": 1461591395,
            "is_org_shared": false,
            "is_user_deleted": false
        },
        {
            "id": "D13E1A3B8",
            "is_im": true,
            "user": "U0QVD73PC",
            "created": 1461591395,
            "is_org_shared": false,
            "is_user_deleted": false
        }
    ]
}
 */

/*
{
    "ok": true,
    "channels": [
        {
            "id": "C036JELTU",
            "name": "general",
            "is_channel": true,
            "created": 1418647486,
            "creator": "U036JELTN",
            "is_archived": false,
            "is_general": true,
            "is_member": false,
            "members": [
                "U036JELTN",
                "U036R7DJB",
                "U036SDG91",
                "U036T85S8",
                "U036UJGD3",
                "U039TREG5",
                "U03CM0U97",
                "U03E84SPW",
                "U03H5LMPS",
                "U04VA95H7",
                "U07R3T65U",
                "U0B0Z1ZK3",
                "U0DRC1QR2",
                "U0KUFG50D",
                "U0QVD73PC",
                "U0XLU3RK9"
            ],
            "topic": {
                "value": "",
                "creator": "",
                "last_set": 0
            },
            "purpose": {
                "value": "This channel is for team-wide communication and announcements. All team members are in this channel.",
                "creator": "",
                "last_set": 0
            },
            "num_members": 13
        },
        {
            "id": "C0A73V0UA",
            "name": "kontor",
            "is_channel": true,
            "created": 1441444932,
            "creator": "U036JELTN",
            "is_archived": false,
            "is_general": false,
            "is_member": false,
            "members": [
                "U036JELTN",
                "U036R7DJB",
                "U036SDG91",
                "U036T85S8",
                "U036UJGD3",
                "U039TREG5",
                "U03E84SPW",
                "U04VA95H7",
                "U07R3T65U",
                "U0QVD73PC"
            ],
            "topic": {
                "value": "",
                "creator": "",
                "last_set": 0
            },
            "purpose": {
                "value": "De s\u00e6tninger man skriver her ender i Trello under \u201cKontor og adm. opgaver\u201d. Med andre ord er det IKKE meningen at man skal diskutere noget som helst i denne tr\u00e5d. F.eks. kan man skrive \"K\u00f8be whiteboard markers i flere forskellige farver\"",
                "creator": "U036JELTN",
                "last_set": 1441445126
            },
            "num_members": 9
        },
        {
            "id": "C07745T5Z",
            "name": "microserviceserrors",
            "is_channel": true,
            "created": 1436108051,
            "creator": "U036JELTN",
            "is_archived": false,
            "is_general": false,
            "is_member": true,
            "members": [
                "U036JELTN",
                "U13EEAATT"
            ],
            "topic": {
                "value": "",
                "creator": "",
                "last_set": 0
            },
            "purpose": {
                "value": "",
                "creator": "",
                "last_set": 0
            },
            "num_members": 2
        },
        {
            "id": "C12GA448K",
            "name": "videostatus",
            "is_channel": true,
            "created": 1461240711,
            "creator": "U036R7DJB",
            "is_archived": false,
            "is_general": false,
            "is_member": false,
            "members": [
                "U036JELTN",
                "U036R7DJB",
                "U036SDG91",
                "U036T85S8",
                "U036UJGD3",
                "U03E84SPW",
                "U04VA95H7",
                "U07R3T65U",
                "U0DRC1QR2",
                "U0KUFG50D",
                "U0QVD73PC",
                "U0XLU3RK9"
            ],
            "topic": {
                "value": "",
                "creator": "",
                "last_set": 0
            },
            "purpose": {
                "value": "Samling af ugestatus videoerne",
                "creator": "U036R7DJB",
                "last_set": 1461240711
            },
            "num_members": 12
        }
    ]
}
 */