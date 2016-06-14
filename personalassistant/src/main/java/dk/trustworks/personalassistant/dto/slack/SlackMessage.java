package dk.trustworks.personalassistant.dto.slack;

/**
 * Created by hans on 31/05/16.
 */
public class SlackMessage {

    public String text;
    public String response_type;
    public byte[] file;
    public String filetype;
    public String filename;

    public SlackMessage() {
    }

    public SlackMessage(String response_type) {
        this.response_type = response_type;
    }

    public SlackMessage(String text, String response_type) {
        this.text = text;
        this.response_type = response_type;
    }

    public SlackMessage(String response_type, byte[] file, String filetype, String filename) {
        this.text = text;
        this.response_type = response_type;
        this.file = file;
        this.filetype = filetype;
        this.filename = filename;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SlackMessage{");
        sb.append("text='").append(text).append('\'');
        sb.append(", response_type='").append(response_type).append('\'');
        //sb.append(", file=").append(Arrays.toString(file));
        sb.append(", filetype='").append(filetype).append('\'');
        sb.append(", filename='").append(filename).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
