package dk.trustworks.personalassistant.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.personalassistant.client.timemanager.Locator;
import dk.trustworks.personalassistant.dto.timemanager.User;
import dk.trustworks.personalassistant.dto.timemanager.Work;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 18/05/15.
 */
public class RestClient {

    public List<Work> getRegisteredWorkByYearMonthDay(int year, int month, int day) {
        System.out.println("RestClient.getRegisteredWorkByYearMonthDay");
        System.out.println("year = [" + year + "], month = [" + month + "], day = [" + day + "]");
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice") + "/api/works/search/findByYearAndMonthAndDay")
                    .queryString("month", month)
                    .queryString("year", year)
                    .queryString("day", day)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<Work> result = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Work>>() {
            });
            return result;
        } catch (UnirestException | IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<User> getUsers() {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/users")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<User>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
