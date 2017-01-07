package dk.trustworks.botmanager.nlp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.botmanager.nlp.dto.NlpResponse;
import dk.trustworks.botmanager.nlp.dto.Result;

import java.io.IOException;

/**
 * Created by hans on 02/06/16.
 */
public class ApiAIClient {

    private static final String ClientToken = "1dca5543466646e49561005118d1c942";

    public static Result sendQuery(String query) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get("https://api.api.ai/v1/query")
                    .header("Authorization", "Bearer " + ClientToken)
                    .queryString("query", query)
                    .queryString("v", "20150910")
                    .queryString("sessionId", "xxx")
                    .queryString("lang", "EN")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return ((NlpResponse)mapper.readValue(response.getRawBody(), new TypeReference<NlpResponse>() {})).getResult();
        } catch (UnirestException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
