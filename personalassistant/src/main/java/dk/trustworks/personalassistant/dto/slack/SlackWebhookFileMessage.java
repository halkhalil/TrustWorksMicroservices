package dk.trustworks.personalassistant.dto.slack;

/**
 * Created by hans on 05/06/16.
 */
public class SlackWebhookFileMessage extends SlackWebhookMessage {

    public final byte[] file;
    public final String filetype;
    public final String filename;

    public SlackWebhookFileMessage(String channel, byte[] file, String filetype, String filename) {
        super(channel);
        this.file = file;
        this.filetype = filetype;
        this.filename = filename;
    }
}

