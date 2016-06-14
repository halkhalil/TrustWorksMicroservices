package dk.trustworks.personalassistant.dto.nlp;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 02/06/16.
 */
public class NlpResponse {

    @JsonProperty("id")
    private String id;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("result")
    private Result result;
    @JsonProperty("status")
    private Status status;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     *
     * @return
     * The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The timestamp
     */
    @JsonProperty("timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    /**
     *
     * @param timestamp
     * The timestamp
     */
    @JsonProperty("timestamp")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     *
     * @return
     * The result
     */
    @JsonProperty("result")
    public Result getResult() {
        return result;
    }

    /**
     *
     * @param result
     * The result
     */
    @JsonProperty("result")
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     *
     * @return
     * The status
     */
    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    @JsonProperty("status")
    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
