package dk.trustworks.botmanager.network.timemanager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.framework.model.*;
import dk.trustworks.framework.network.Locator;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 18/05/15.
 */
public class RestClient {

    public List<User> getUsers() {
        System.out.println("RestClient.getUsers");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/users/v2")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<User>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public User findBySlackUsername(String slackusername) {
        System.out.println("RestClient.findBySlackUsername");
        System.out.println("slackusername = [" + slackusername + "]");
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/users/search/findBySlackUsername")
                    .header("accept", "application/json")
                    .queryString("slackusername", slackusername)
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<User>() {});
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updatePassword(String password, String useruuid) {
        System.out.println("RestClient.updatePassword");
        System.out.println("password = [" + password + "], useruuid = [" + useruuid + "]");

        try {
            Unirest.post(Locator.getInstance().resolveURL("userservice") + "/api/users/"+useruuid+"/password")
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(password)
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
