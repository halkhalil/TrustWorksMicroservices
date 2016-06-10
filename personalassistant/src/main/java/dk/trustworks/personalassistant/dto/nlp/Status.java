package dk.trustworks.personalassistant.dto.nlp;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 02/06/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "code",
        "errorType"
})
public class Status {

    @JsonProperty("code")
    private int code;
    @JsonProperty("errorType")
    private String errorType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The code
     */
    @JsonProperty("code")
    public int getCode() {
        return code;
    }

    /**
     *
     * @param code
     * The code
     */
    @JsonProperty("code")
    public void setCode(int code) {
        this.code = code;
    }

    /**
     *
     * @return
     * The errorType
     */
    @JsonProperty("errorType")
    public String getErrorType() {
        return errorType;
    }

    /**
     *
     * @param errorType
     * The errorType
     */
    @JsonProperty("errorType")
    public void setErrorType(String errorType) {
        this.errorType = errorType;
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
        final StringBuilder sb = new StringBuilder("Status{");
        sb.append("code=").append(code);
        sb.append(", errorType='").append(errorType).append('\'');
        sb.append(", additionalProperties=").append(additionalProperties);
        sb.append('}');
        return sb.toString();
    }
}