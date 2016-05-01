package dk.trustworks.personalassistant;

import dk.trustworks.personalassistant.topics.*;
import flowctrl.integration.slack.SlackClientFactory;
import flowctrl.integration.slack.rtm.Event;
import flowctrl.integration.slack.rtm.SlackRealTimeMessagingClient;
import flowctrl.integration.slack.type.Channel;
import flowctrl.integration.slack.webapi.SlackWebApiClient;
import flowctrl.integration.slack.webhook.SlackWebhookClient;

/**
 * Created by hans on 20/01/16.
 */
public class App {

    //public static final String channel = "U036JELTN";

    static Topic topics[] = {new Mother(), new Pictures(), new Templates(), new FileSearch(), new Connect()};

    public static void main(final String[] args) throws Exception {
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