package dk.trustworks.personalassistant.dto;

/**
 * Created by hans on 30/05/16.
 */
public class Command {
    public String token;
    public String team_id;
    public String team_domain;
    public String channel_id;
    public String channel_name;
    public String user_id;
    public String user_name;
    public String command;
    public String text;
    public String response_url;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Command{");
        sb.append("token='").append(token).append('\'');
        sb.append(", team_id='").append(team_id).append('\'');
        sb.append(", team_domain='").append(team_domain).append('\'');
        sb.append(", channel_id='").append(channel_id).append('\'');
        sb.append(", channel_name='").append(channel_name).append('\'');
        sb.append(", user_id='").append(user_id).append('\'');
        sb.append(", user_name='").append(user_name).append('\'');
        sb.append(", command='").append(command).append('\'');
        sb.append(", text='").append(text).append('\'');
        sb.append(", response_url='").append(response_url).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
