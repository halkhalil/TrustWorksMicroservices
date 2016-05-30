package dk.trustworks.personalassistant.service;

import dk.trustworks.personalassistant.dto.Command;
import dk.trustworks.personalassistant.topics.*;
import org.jooby.Result;

/**
 * Created by hans on 30/05/16.
 */
public class CommandService {

    static Topic topics[] = {new Mother(), new Pictures(), new Templates(), new FileSearch(), new Connect()};

    public Result create(Command command) {
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
            //actualTopic.postResponse(command, jsonNode.get("channel").asText(), jsonNode.get("user").asText());
        }
        return new Result();
    }
}
