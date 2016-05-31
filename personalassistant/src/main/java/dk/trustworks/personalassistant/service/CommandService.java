package dk.trustworks.personalassistant.service;

import dk.trustworks.personalassistant.dto.Command;
import dk.trustworks.personalassistant.topics.*;
import org.jooby.Result;

/**
 * Created by hans on 30/05/16.
 */
public class CommandService {

    static Topic topics[] = {new Mother(), new Pictures(), new Templates(), new FileSearch(), new Connect()};

    // command =
    // Command{token='P5i2tVZ0P9WGGMsT0FkRhks2',
    // team_id='T036JELTL',
    // team_domain='trustworks',
    // channel_id='D13E9H2N9',
    // channel_name='directmessage',
    // user_id='U036JELTN',
    // user_name='hans',
    // command='/mother',
    // text='test',
    // response_url='https://hooks.slack.com/commands/T036JELTL/46826048199/1nsSqrp5LgT7gLzFQ2Is5cQf'}

    public String create(Command command) {
        //if(!jsonNode.get("text").asText().contains("<@U13EEAATT>")) return;
        System.out.println("command = " + command);
        Topic actualTopic = null;
        double highScore = 0.0;
        for (Topic topic : topics) {
            double probability = topic.getProbability(command.text);
            if(probability> 0.8 && (actualTopic == null || probability > highScore)) {
                actualTopic = topic;
                highScore = probability;
            }
        }
        if(actualTopic != null) {
            System.out.println("actualTopic = " + actualTopic);
            actualTopic.postResponse(command.text, command.response_url, command.user_name);
        }
        return "Just a second...";
    }
}
