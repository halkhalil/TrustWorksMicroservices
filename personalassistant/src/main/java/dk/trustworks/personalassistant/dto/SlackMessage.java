package dk.trustworks.personalassistant.dto;

/**
 * Created by hans on 31/05/16.
 */
public class SlackMessage {

    public String text;
    public String response_type;

    public SlackMessage() {
    }

    public SlackMessage(String text, String response_type) {
        this.text = text;
        this.response_type = response_type;
    }
}
