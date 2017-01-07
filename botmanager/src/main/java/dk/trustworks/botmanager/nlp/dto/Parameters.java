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
        "geo-city",
        "geo-country",
        "given-name"
})
public class Parameters {

    @JsonProperty("geo-city")
    private String geoCity;
    @JsonProperty("geo-country")
    private String geoCountry;
    @JsonProperty("given-name")
    private String givenName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The geoCity
     */
    @JsonProperty("geo-city")
    public String getGeoCity() {
        return geoCity;
    }

    /**
     *
     * @param geoCity
     * The geo-city
     */
    @JsonProperty("geo-city")
    public void setGeoCity(String geoCity) {
        this.geoCity = geoCity;
    }

    /**
     *
     * @return
     * The geoCountry
     */
    @JsonProperty("geo-country")
    public String getGeoCountry() {
        return geoCountry;
    }

    /**
     *
     * @param geoCountry
     * The geo-country
     */
    @JsonProperty("geo-country")
    public void setGeoCountry(String geoCountry) {
        this.geoCountry = geoCountry;
    }

    /**
     *
     * @return
     * The givenName
     */
    @JsonProperty("given-name")
    public String getGivenName() {
        return givenName;
    }

    /**
     *
     * @param givenName
     * The given-name
     */
    @JsonProperty("given-name")
    public void setGivenName(String givenName) {
        this.givenName = givenName;
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
        final StringBuilder sb = new StringBuilder("Parameters{");
        sb.append("geoCity='").append(geoCity).append('\'');
        sb.append(", geoCountry='").append(geoCountry).append('\'');
        sb.append(", givenName='").append(givenName).append('\'');
        sb.append(", additionalProperties=").append(additionalProperties);
        sb.append('}');
        return sb.toString();
    }
}
