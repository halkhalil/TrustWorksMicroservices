package dk.trustworks.personalassistant.dto.nlp;

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
        "intentId",
        "webhookUsed",
        "intentName"
})
public class Metadata {

    @JsonProperty("intentId")
    private String intentId;
    @JsonProperty("webhookUsed")
    private String webhookUsed;
    @JsonProperty("intentName")
    private String intentName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The intentId
     */
    @JsonProperty("intentId")
    public String getIntentId() {
        return intentId;
    }

    /**
     *
     * @param intentId
     * The intentId
     */
    @JsonProperty("intentId")
    public void setIntentId(String intentId) {
        this.intentId = intentId;
    }

    /**
     *
     * @return
     * The webhookUsed
     */
    @JsonProperty("webhookUsed")
    public String getWebhookUsed() {
        return webhookUsed;
    }

    /**
     *
     * @param webhookUsed
     * The webhookUsed
     */
    @JsonProperty("webhookUsed")
    public void setWebhookUsed(String webhookUsed) {
        this.webhookUsed = webhookUsed;
    }

    /**
     *
     * @return
     * The intentName
     */
    @JsonProperty("intentName")
    public String getIntentName() {
        return intentName;
    }

    /**
     *
     * @param intentName
     * The intentName
     */
    @JsonProperty("intentName")
    public void setIntentName(String intentName) {
        this.intentName = intentName;
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
