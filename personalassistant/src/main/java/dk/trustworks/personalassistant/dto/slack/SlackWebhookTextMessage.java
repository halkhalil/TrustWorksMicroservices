package dk.trustworks.personalassistant.dto.slack;

/**
 * Created by hans on 05/06/16.
 */
public class SlackWebhookTextMessage extends SlackWebhookMessage {

    public final String text;

    public SlackWebhookTextMessage(String channel, String text) {
        super(channel);
        this.text = text;
    }
}
