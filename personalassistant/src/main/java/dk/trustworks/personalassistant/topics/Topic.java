package dk.trustworks.personalassistant.topics;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by hans on 25/04/16.
 */
public abstract class Topic {
    //protected String[] topics = {};
    //public double score = 0.0;

    public abstract double getProbability(String command);

    protected TopicScore getSpecificProbability(String text, String[] topics) {
        TopicScore topicScore = new TopicScore();
        for (String topic : topics) {
            for (String word : text.split(" ")) {
                double distance = StringUtils.getJaroWinklerDistance(word.toLowerCase(), topic.toLowerCase());
                if(distance > topicScore.score) {
                    topicScore.score = distance;
                    topicScore.topic = topic;
                }
            }
        }
        return topicScore;
    }

    class TopicScore {
        String topic;
        double score;
    }

    public abstract void postResponse(String command, String recipient, String user);
}
