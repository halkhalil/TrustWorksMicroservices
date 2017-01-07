package dk.trustworks.botmanager.nlp.dto;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 02/06/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "speech"
})
public class Fulfillment {

    @JsonProperty("speech")
    private String speech;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The speech
     */
    @JsonProperty("speech")
    public String getSpeech() {
        return speech;
    }

    /**
     *
     * @param speech
     * The speech
     */
    @JsonProperty("speech")
    public void setSpeech(String speech) {
        this.speech = speech;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Fulfillment{");
        sb.append("speech='").append(speech).append('\'');
        sb.append(", additionalProperties=").append(additionalProperties);
        sb.append('}');
        return sb.toString();
    }
}
