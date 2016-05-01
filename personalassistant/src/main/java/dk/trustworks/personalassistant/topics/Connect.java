package dk.trustworks.personalassistant.topics;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Created by hans on 25/04/16.
 */
public class Connect extends Topic {

    private String topics[] = {"connect"};
    private String recipient = "";
    private String user;

    public Connect() {
    }

    public double getProbability(String command) {
        return getSpecificProbability(command, topics).score;
    }

    @Override
    public void postResponse(String command, String recipient, String user) {
        this.recipient = recipient;
        this.user = user;
        connectToIMChannel();
    }

    private void connectToIMChannel() {
        System.out.println("Connect.connectToIMChannel");
        String username = "";
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post("https://slack.com/api/users.info")
                    .header("accept", "application/json")
                    .field("token", "xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61")
                    .field("user", user)
                    .asJson();
            username = jsonResponse.getBody().getObject().getJSONObject("user").getString("name");
            System.out.println("jsonResponse.getBody().getObject().toString() = " + jsonResponse.getBody().getObject().toString());
            System.out.println("username = " + username);
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post("https://slack.com/api/chat.postMessage")
                    .header("accept", "application/json")
                    .field("token", "xoxb-37490350945-2eVzVkvuHkNPlGJ96bcsHw61")
                    .field("channel", "@"+username)
                    .field("as_user", "true")
                    .field("text", "...connected")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
