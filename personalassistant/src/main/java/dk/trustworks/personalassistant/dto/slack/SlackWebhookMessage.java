package dk.trustworks.personalassistant.dto.slack;

/**
 * Created by hans on 05/06/16.
 */
public class SlackWebhookMessage {
    public String channel;
    public final boolean as_user;
    public final String username;

    private SlackWebhookMessage() {
        username = "mother";
        as_user = true;
    }

    public SlackWebhookMessage(String channel) {
        this();
        this.channel = channel;
    }
}
